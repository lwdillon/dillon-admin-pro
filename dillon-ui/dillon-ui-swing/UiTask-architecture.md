# UiTask 框架架构设计

## 设计目标

`dillon-ui-swing` 里的异步调用原先主要有三种写法：

1. 直接写 `CompletableFuture.supplyAsync(...).thenAcceptAsync(..., SwingUtilities::invokeLater)`
2. 使用旧的 `ExecuteUtils`
3. 新增但尚未完全落地的 `SwingAsyncExecutor` / `SwingUiTask`

这些写法都能工作，但会带来几个长期问题：

1. 线程池来源不一致，默认公共线程池容易和其它异步任务互相影响。
2. 页面层重复处理 EDT 切换、loading、异常解包和收尾逻辑。
3. 多阶段任务缺少统一上下文，业务流程一长就会退化成嵌套回调。
4. 简单任务和复杂任务没有明确分层，API 容易继续扩散。

UiTask 框架的目标是把这些问题拆成稳定的三层。

## 分层结构

### 1. 运行时层

类：`com.dillon.lw.swing.async.UiTaskRuntime`

职责：

1. 提供 Swing 模块专用后台线程池。
2. 统一 EDT 调度和阻塞式 EDT 调用。
3. 提供整体超时控制。
4. 统一解包 `CompletionException` / `ExecutionException`。

这一层不感知业务，也不感知页面组件，只负责“怎么执行”。

### 2. 上下文层

类：`com.dillon.lw.swing.async.UiTaskContext`

职责：

1. 作为任务链的中间结果容器。
2. 让每一步可以通过 key 读取前一步结果。
3. 避免页面代码自己维护大量局部变量和临时 holder。

典型场景：

```java
SwingUiTask.start("loginResp", loginTask)
        .taskWithResult("permission", ctx -> permissionApi(ctx.get("loginResp")))
        .onUi(ctx -> render(ctx.get("permission")))
        .execute();
```

### 3. DSL 门面层

类：

1. `com.dillon.lw.swing.async.SwingUiTask`
2. `com.dillon.lw.utils.SwingAsyncExecutor`

职责分工：

1. `SwingAsyncExecutor`：单任务、批量任务、轻量 flow。适合列表刷新、按钮提交、批量查询。
2. `SwingUiTask`：上下文式多阶段任务链。适合登录、初始化、跨接口串联流程。

这两者共用同一个 `UiTaskRuntime`，这样“入口不同、运行规则一致”。

## 推荐使用边界

### 用 `SwingAsyncExecutor`

满足下面任一条件即可：

1. 只有一个后台接口调用。
2. 多个后台任务互不依赖，只需要最终汇总。
3. 不需要显式保存多个阶段结果。

### 用 `SwingUiTask`

满足下面任一条件建议直接使用：

1. 后一步依赖前一步结果。
2. 页面需要把多个中间结果在末端统一渲染。
3. 任务链里要显式穿插 EDT 步骤。
4. 页面层已经开始出现“先查 A，再查 B，再更新 UI”的流程代码。

## 执行生命周期

`SwingUiTask` 的统一生命周期如下：

1. `execute()` 时才真正开始构建和执行任务链，避免 builder 阶段提前触发异步任务。
2. 执行前在 EDT 中触发 `beforeLoading` 和 `WaitPane.showMessageLayer()`。
3. 后台阶段进入 `UiTaskRuntime` 专用线程池。
4. UI 阶段强制切回 EDT，同步执行，保证顺序。
5. 成功后进入 `onUi`。
6. 失败后统一进入 `onError` 或 `SwingExceptionHandler`。
7. 最终统一关闭 `WaitPane`、执行 `afterLoading` 和 `onComplete`。

## 迁移策略

建议按下面顺序收敛：

1. 新增页面优先只用 `SwingAsyncExecutor` 或 `SwingUiTask`，不要再直接散写 `CompletableFuture + SwingUtilities`。
2. 简单列表页先迁到 `SwingAsyncExecutor`。
3. 登录、初始化、联动加载等流程迁到 `SwingUiTask`。
4. 等主要页面收敛后，再删除残余的手写异步模板。

## 当前落地点

本次设计已经把以下结构落入模块：

1. `SwingUiTask`：补齐 `LoginPane` 依赖的上下文式任务流。
2. `UiTaskRuntime`：作为共享执行内核。
3. `UiTaskContext`：作为多阶段结果容器。
4. `SwingAsyncExecutor`：继续保留为轻量入口，并接入共享运行时。

这样后续扩展时，只需要决定“用简单任务入口还是用上下文任务入口”，不需要再重新发明线程切换和异常闭环。
