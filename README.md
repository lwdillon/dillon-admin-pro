# Dillon Admin Pro

基于 `ruoyi-vue-pro` 改造的桌面端权限管理系统，提供一套后端服务，并同时实现了 `JavaFX` 与 `Swing` 两套桌面客户端。

项目目标是把常见的后台管理能力搬到桌面端场景中，适合做管理工作台、内网业务系统、客户端运维工具等桌面化实践。

## 交流沟通

欢迎加入 QQ 群交流：

* QQ 群 1：`114697782`
* QQ 群 2：`808309284`

## 项目特点

* 同一套后端接口，同时支持 `JavaFX` 和 `Swing` 客户端
* UI 风格参考 `yudao-ui-admin-vue3`，桌面端交互做了适配
* 内置用户、角色、菜单、部门、岗位、字典、通知、日志、文件、定时任务等后台能力
* 客户端内置服务端地址配置、自动更新配置、WebSocket 配置
* 后端基于 `Spring Boot 2.7.x` + `MyBatis Plus` + `Redis` + `Quartz`

## 技术栈

### 服务端

* Java 8
* Spring Boot 2.7.18
* MyBatis Plus
* Redis
* Quartz
* Maven 多模块

### JavaFX 客户端

* Java 23
* JavaFX 25.0.1
* [AtlantaFX](https://github.com/mkpaz/atlantafx)
* [Ikonli](https://github.com/kordamp/ikonli)
* [AnimateFX](https://github.com/Typhon0/AnimateFX)

### Swing 客户端

* Java 8
* [FlatLaf](https://www.formdev.com/flatlaf/)
* SwingX
* MigLayout
* JFreeChart

## 项目结构

* `dillon-server`：后端启动模块，提供 REST API
* `dillon-module-system`：系统管理业务模块
* `dillon-module-infra`：基础设施模块，包含文件、任务、日志等能力
* `dillon-ui/dillon-ui-common`：桌面端公共 API、VO、客户端基础能力
* `dillon-ui/dillon-ui-swing`：Swing 客户端
* `dillon-ui/dillon-ui-fx`：JavaFX 客户端
* `dillon-framework`：项目公共框架与各类 starter
* `dillon-dependencies`：依赖版本管理 BOM

如果你是第一次接手这个仓库，可以这样理解：

* `dillon-server` 更像是后端聚合启动器，真正的业务能力主要在 `dillon-module-*`
* `dillon-ui-common` 负责给两个桌面端复用接口调用、数据结构和通用客户端能力
* `dillon-ui-swing` 与 `dillon-ui-fx` 是两套独立客户端实现，后端接口基本一致，但界面组件和启动方式不同
* `dillon-framework` 是整个项目的基础设施层，封装了安全、缓存、任务、Web、MyBatis 等公共能力

## 环境要求

* Maven 3.9+
* MySQL 8+
* Redis 6+
* JDK 8：后端、公共模块、Swing 客户端
* JDK 23：JavaFX 客户端

> 根工程启用了 Maven Toolchains。  
> 如果你要在根目录直接执行全量构建，建议在本机同时准备 `JDK 8` 和 `JDK 23`。

`~/.m2/toolchains.xml` 可参考：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>1.8</version>
    </provides>
    <configuration>
      <jdkHome>/path/to/jdk8</jdkHome>
    </configuration>
  </toolchain>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>23</version>
    </provides>
    <configuration>
      <jdkHome>/path/to/jdk23</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

## 快速开始

### 1. 初始化数据库

创建数据库，例如：

```sql
CREATE DATABASE `dillon-admin-pro` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后导入以下脚本：

* `sql/mysql/dillon-admin-pro.sql`
* `sql/mysql/quartz.sql`

说明：

* `dillon-admin-pro.sql` 是业务主库初始化脚本
* `quartz.sql` 是定时任务相关表结构
* 默认本地库名使用 `dillon-admin-pro`，如果你改了库名，需要同步修改 `application-local.yaml`

### 2. 启动 Redis

默认本地配置使用：

* Host: `127.0.0.1`
* Port: `6379`
* DB: `0`

如果你的 Redis 配置不同，请修改 `dillon-server/src/main/resources/application-local.yaml`。

### 3. 修改后端本地配置

默认本地开发配置文件：

* `dillon-server/src/main/resources/application.yaml`
* `dillon-server/src/main/resources/application-local.yaml`

重点检查这些配置是否符合你的本地环境：

* `server.port`，默认 `48080`
* `spring.datasource.dynamic.datasource.master`
* `spring.datasource.dynamic.datasource.slave`
* `spring.redis`

默认本地配置中，数据库连接示例为：

* 数据库：`dillon-admin-pro`
* 用户名：`root`
* 端口：`3306`

如果你只是本地单库调试，`slave` 可以继续指向同一个库。

默认 profile 为：

```yaml
spring:
  profiles:
    active: local
```

### 4. 启动后端

直接运行主类：

```text
com.dillon.lw.server.DillonServerApplication
```

启动后可访问：

* Swagger UI：`http://127.0.0.1:48080/swagger-ui`
* Actuator：`http://127.0.0.1:48080/actuator`

项目接口默认前缀为：

* `http://127.0.0.1:48080/admin-api`

如果服务正常启动，访问未登录接口时通常会返回类似“账号未登录”的响应，这说明服务端已经可用。

### 5. 启动桌面客户端

#### JavaFX

运行主类：

```text
com.dillon.lw.fx.AppStart
```

说明：

* JavaFX 模块单独要求 `JDK 23`
* 首次启动如果是 IDE 工程运行，建议确认 IDE 的 Project SDK 和 Module SDK 没有仍指向 JDK 8

#### Swing

运行主类：

```text
com.dillon.lw.DillonSwingUiApplication
```

说明：

* Swing 客户端基于 `JDK 8`
* 如果你只是做后端联调或传统桌面端调试，Swing 客户端是更轻量的本地入口

## 客户端配置

客户端默认读取以下文件：

* `dillon-ui/dillon-ui-fx/src/main/resources/application.properties`
* `dillon-ui/dillon-ui-swing/src/main/resources/application.properties`

两个客户端都会在启动早期读取各自模块里的 `application.properties`，并把配置写入 `System Properties`，后续 HTTP 请求、自动更新、WebSocket 等能力都从这里取值。

也就是说，日常联调时优先改这两个文件即可。

默认配置示例：

```properties
app.server.master.ip=127.0.0.1
app.server.slave.ip=127.0.0.1
app.server.port=48080
app.server.basePath=admin-api
app.server.scheme=http

app.update.enabled=true
app.update.metadata.url=

app.websocket.enable=true
app.websocket.path=/infra/ws
app.websocket.master.url=ws://127.0.0.1:48080/infra/ws
app.websocket.slave.url=ws://127.0.0.1:48080/infra/ws
```

如果你的服务端地址、协议、上下文路径或 WebSocket 地址不同，请按实际环境调整。

### 配置加载规则

客户端配置建议按下面这套规则理解：

* HTTP 接口访问会基于 `app.server.*` 组合出基础地址
* HTTP 默认支持主备切换，优先走 `master`，主服务不可用时会切到 `slave`
* WebSocket 优先使用 `app.websocket.master.url` / `app.websocket.slave.url`
* 如果 WebSocket 主备地址留空，客户端会用 `app.server.scheme`、`app.server.master.ip`、`app.server.slave.ip`、`app.server.port`、`app.websocket.path` 自动拼接
* 自动更新默认会在客户端启动后异步检查，不会阻塞主界面显示

### 基础连接配置

这组配置决定客户端调用哪个服务端：

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `app.name` | `dillon-admin` / `dillon-admin-pro-swing` | 客户端名称，用于窗口标题、部分平台的应用名展示 |
| `app.version` | `1.0.0` | 当前客户端本地版本号，自动更新时会拿它和服务端版本比较 |
| `app.server.master.ip` | `127.0.0.1` | 主服务地址 |
| `app.server.slave.ip` | `127.0.0.1` | 备服务地址；没有主备切换需求时可和主地址保持一致 |
| `app.server.port` | `48080` | 服务端端口 |
| `app.server.basePath` | `admin-api` | 服务端接口上下文路径 |
| `app.server.scheme` | `http` | 协议；如果服务端挂在 HTTPS，改成 `https` |

组合后的典型接口前缀是：

```text
http://127.0.0.1:48080/admin-api
```

如果改错这里，最常见的现象是：

* 登录接口 404
* 菜单加载失败
* 查询列表报网络异常
* WebSocket 连不上

### 主备切换说明

客户端内置了一套简单的主备策略：

* 默认优先访问 `app.server.master.ip`
* 如果主机失败，请求层会切换到 `app.server.slave.ip`
* 后台会周期性探测主机是否恢复
* 主机恢复后，请求会重新切回主机

所以在以下场景中，这组配置比较有用：

* 内网系统需要接双机热备
* 生产环境区分主机与灾备机
* 本地联调时想模拟主备切换

如果你只是单机开发，直接把 `master` 和 `slave` 都写成同一个地址最省事。

### 自动更新配置

客户端默认提供启动后自动检查更新的能力。

相关字段如下：

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `app.update.enabled` | `true` | 是否启用自动更新检查 |
| `app.update.metadata.url` | 空 | 预留字段 |
| `app.update.connect.timeout.ms` | `5000` | 预留字段 |
| `app.update.read.timeout.ms` | `10000` | 预留字段 |

当前代码里的实际行为是：

* Swing 客户端调用 `GET /admin-api/infra/client/update/latest/swing`
* JavaFX 客户端调用 `GET /admin-api/infra/client/update/latest/javafx`
* Swing 下载地址是 `GET /admin-api/infra/client/update/download/swing`
* JavaFX 下载地址是 `GET /admin-api/infra/client/update/download/javafx`

说明：

* `app.update.enabled=false` 可以直接关闭自动更新检查
* `app.version` 会作为本地版本号参与比较
* 当前代码里 `app.update.metadata.url`、`app.update.connect.timeout.ms`、`app.update.read.timeout.ms` 还属于预留配置，暂未在客户端更新逻辑中实际使用
* 自动更新主要适用于客户端以 `jar` 方式运行；如果是在 IDE 中直接运行主类，通常会跳过 jar 替换流程

如果你想让服务端真正提供更新包，还需要在后端配置：

* `dillon-server/src/main/resources/application-local.yaml`

对应字段包括：

* `dillon.client-update.enabled`
* `dillon.client-update.swing.version`
* `dillon.client-update.swing.jar-file`
* `dillon.client-update.javafx.version`
* `dillon.client-update.javafx.jar-file`

### WebSocket 配置

WebSocket 主要用于实时通知、消息推送、在线状态等场景。

相关字段如下：

| 配置项 | 默认值 | 说明 |
|------|------|------|
| `app.websocket.enable` | `true` | 是否启用 WebSocket |
| `app.websocket.path` | `/infra/ws` | WebSocket 路径 |
| `app.websocket.master.url` | `ws://127.0.0.1:48080/infra/ws` | 主 WebSocket 地址 |
| `app.websocket.slave.url` | `ws://127.0.0.1:48080/infra/ws` | 备 WebSocket 地址 |
| `app.websocket.reconnect.delay.ms` | `5000` | 断线重连间隔 |

这组配置的优先级是：

* 如果显式配置了 `app.websocket.master.url` / `app.websocket.slave.url`，客户端优先使用它们
* 如果这两个值为空，客户端会自动按下面规则拼接：

```text
ws://{app.server.master.ip}:{app.server.port}{app.websocket.path}
wss://{app.server.master.ip}:{app.server.port}{app.websocket.path}
```

其中：

* 当 `app.server.scheme=http` 时，会转成 `ws`
* 当 `app.server.scheme=https` 时，会转成 `wss`

如果你的服务端走了网关、反向代理或者统一域名，建议直接把 `app.websocket.master.url` 写成完整地址，避免路径拼接和代理转发不一致。

### 常见配置场景

#### 1. 本地开发单机

```properties
app.server.master.ip=127.0.0.1
app.server.slave.ip=127.0.0.1
app.server.port=48080
app.server.basePath=admin-api
app.server.scheme=http
app.websocket.master.url=ws://127.0.0.1:48080/infra/ws
app.websocket.slave.url=ws://127.0.0.1:48080/infra/ws
```

#### 2. 测试环境 HTTPS + 域名

```properties
app.server.master.ip=test-api.example.com
app.server.slave.ip=test-api.example.com
app.server.port=443
app.server.basePath=admin-api
app.server.scheme=https
app.websocket.master.url=wss://test-api.example.com/infra/ws
app.websocket.slave.url=wss://test-api.example.com/infra/ws
```

如果你的 WebSocket 也是通过网关并挂在自定义前缀下，就把这里改成网关实际暴露出来的完整地址。

#### 3. 生产环境主备

```properties
app.server.master.ip=10.0.0.10
app.server.slave.ip=10.0.0.11
app.server.port=48080
app.server.basePath=admin-api
app.server.scheme=http
app.websocket.master.url=ws://10.0.0.10:48080/infra/ws
app.websocket.slave.url=ws://10.0.0.11:48080/infra/ws
app.websocket.reconnect.delay.ms=5000
```

### 排查建议

如果客户端配置后仍然不通，建议按这个顺序排查：

1. 先确认后端是否真的启动在 `48080`，并且 `/admin-api` 可访问。
2. 再确认客户端里的 `app.server.basePath` 是否和服务端上下文一致。
3. 如果接口通但实时消息不通，再单独检查 `app.websocket.*`。
4. 如果只是想先跑通主流程，可以先把 `app.update.enabled=false`，排除更新检查干扰。

## 构建说明

### 全量构建

当本地已配置好 `JDK 8` 和 `JDK 23` 的 Maven Toolchains 后，可在根目录执行：

```bash
mvn clean package -DskipTests
```

适用场景：

* 需要一次性产出服务端、公共模块、Swing、JavaFX 全部构件
* 做发布前自检，确认多模块整体没有编译问题

### 只构建服务端

```bash
mvn -pl dillon-server -am clean package -DskipTests
```

适用场景：

* 只改了后端业务或配置
* 想更快验证接口层、服务层、数据库映射是否正常

### 只构建 Swing 客户端

```bash
mvn -pl dillon-ui/dillon-ui-swing -am clean package -DskipTests
```

适用场景：

* 做 Swing 页面、交互、接口联调
* 当前机器没有配好 JavaFX 运行环境

### 只构建 JavaFX 客户端

```bash
mvn -pl dillon-ui/dillon-ui-fx -am clean package -DskipTests
```

适用场景：

* 专注 JavaFX 客户端开发
* 需要验证 JavaFX 相关依赖和模块是否正常

如果你当前只需要服务端和 Swing，也可以先跳过 JavaFX 模块：

```bash
mvn -pl dillon-server,dillon-ui/dillon-ui-swing -am clean package -DskipTests
```

## 开发说明

* 本项目基于 [芋道源码 / ruoyi-vue-pro](https://github.com/YunaiV/ruoyi-vue-pro) 改造
* 界面设计参考 [yudao-ui-admin-vue3](https://github.com/yudaocode/yudao-ui-admin-vue3)
* 为适配桌面端菜单路由，后端 `system_menu` 表增加了 `component_swing` 和 `component_fx` 两个字段
* Swing 客户端当前已补齐登录前非空校验、防重复点击、回车登录、退出清理本地会话等交互

开发时建议关注下面几个点：

* 如果新增菜单页面，除了后端菜单数据外，还要同步维护对应客户端页面类映射
* 如果接口路径、服务端口或上下文路径有调整，记得同步修改两个客户端的 `application.properties`
* 如果做的是根工程全量编译问题，先优先检查 Maven Toolchains 和本地 JDK 版本
* 如果只做桌面端界面开发，优先使用对应客户端模块单独构建，反馈会更快

## 常见问题

### 1. 为什么根目录编译会报 JDK 版本不匹配？

因为仓库里同时包含：

* 基于 `JDK 8` 的服务端与 Swing 模块
* 基于 `JDK 23` 的 JavaFX 模块

根工程使用了 Maven Toolchains，所以本机如果缺少对应版本 JDK，就容易在全量构建时失败。

### 2. 为什么客户端启动后连不上后端？

优先检查以下几项：

* 后端是否已经成功启动在 `48080`
* 客户端里的 `app.server.basePath` 是否仍为 `admin-api`
* `app.server.scheme` 是否与后端一致
* WebSocket 地址是否和后端 `dillon.websocket.path` 配置一致

### 3. 为什么本地数据库改了名字后服务无法启动？

因为 `dillon-server/src/main/resources/application-local.yaml` 中默认写的是：

* 数据库名：`dillon-admin-pro`

改库名后，需要同时调整主从数据源 URL。

## 安全提示

* 不要把生产环境账号、密码、Token、私钥提交到仓库
* 当前配置文件中包含不少本地开发/演示配置，部署前请务必替换为你自己的环境参数
* 建议把敏感配置迁移到环境变量、外部配置中心或本地未跟踪文件中

## 开源协作

欢迎提交 Issue 和 PR。

建议流程：

* 创建功能分支：`codex/feature-xxx` 或 `codex/fix-xxx`
* 提交前确保构建通过，必要时补充测试
* PR 描述说明改动背景、方案、影响范围和验证方式

推荐提交信息格式：

* `feat(swing): 优化登录交互`
* `fix(server): 修复菜单映射问题`
* `docs(readme): 更新启动说明`

## 参考资料

* 芋道快速开始文档：[https://doc.iocoder.cn/quick-start/](https://doc.iocoder.cn/quick-start/)
* 芋道视频教程：[https://doc.iocoder.cn/video/](https://doc.iocoder.cn/video/)
* 演示地址（Vue3）：[http://dashboard-vue3.yudao.iocoder.cn](http://dashboard-vue3.yudao.iocoder.cn)

## 系统功能

|    | 功能 | 描述 |
|----|------|------|
|    | 用户管理 | 用户是系统操作者，该功能主要完成系统用户配置 |
| ⭐️ | 在线用户 | 当前系统中活跃用户状态监控，支持手动踢下线 |
|    | 角色管理 | 角色菜单权限分配、设置角色按机构进行数据范围权限划分 |
|    | 菜单管理 | 配置系统菜单、操作权限、按钮权限标识等 |
|    | 部门管理 | 配置系统组织机构，树结构展现支持数据权限 |
|    | 岗位管理 | 配置系统用户所属担任职务 |
|    | 字典管理 | 维护系统中常用的固定数据 |
| 🚀 | 操作日志 | 系统正常操作日志记录和查询 |
| ⭐️ | 登录日志 | 系统登录日志记录查询，包含登录异常 |
|    | 通知公告 | 系统通知公告信息发布维护 |

## 界面预览

下面保留完整界面截图，方便直接对照功能模块和视觉效果。

### JavaFX

| 模块       | 浅色                                        | 深色                                        | glass                                     |
|----------|-------------------------------------------|-------------------------------------------|-------------------------------------------|
| 登录       | ![登录](/.image/javafx/登录.png)              | -                                         | -                                         |
| 主页       | ![主页](/.image/javafx/主页1.png)             | ![主页](/.image/javafx/主页2.png)             | ![主页](/.image/javafx/主页3.png)             |
| 主页2      | ![主页2](/.image/javafx/主页21.png)           | ![主页2](/.image/javafx/主页22.png)           | ![主页2](/.image/javafx/主页23.png)           |
| 用户管理     | ![用户管理](/.image/javafx/用户管理1.png)         | ![用户管理](/.image/javafx/用户管理2.png)         | ![用户管理](/.image/javafx/用户管理3.png)         |
| 角色管理     | ![角色管理1](/.image/javafx/角色管理1.png)        | ![角色管理2](/.image/javafx/角色管理2.png)        | ![用户管理3](/.image/javafx/用户管理3.png)        |
| 菜单管理     | ![菜单管理1](/.image/javafx/菜单管理1.png)        | ![菜单管理2](/.image/javafx/菜单管理2.png)        | ![菜单管理3](/.image/javafx/菜单管理3.png)        |
| 部门管理     | ![部门管理](/.image/javafx/部门管理1.png)         | ![部门管理](/.image/javafx/部门管理2.png)         | ![部门管理](/.image/javafx/部门管理3.png)         |
| 岗位管理     | ![岗位管理](/.image/javafx/岗位管理1.png)         | ![岗位管理](/.image/javafx/岗位管理2.png)         | ![岗位管理](/.image/javafx/岗位管理3.png)         |
| 字典管理     | ![字典管理](/.image/javafx/字典管理1.png)         | ![字典管理](/.image/javafx/字典管理2.png)         | ![字典管理](/.image/javafx/字典管理3.png)         |
| 字典数据     | ![字典数据](/.image/javafx/字典数据1.png)         | ![字典数据](/.image/javafx/字典数据2.png)         | ![字典数据](/.image/javafx/字典数据3.png)         |
| 通知公告     | ![通知公告](/.image/javafx/通知公告1.png)         | ![通知公告](/.image/javafx/通知公告2.png)         | ![通知公告](/.image/javafx/通知公告3.png)         |
| 消息模板     | ![消息模板](/.image/javafx/消息模板1.png)         | ![消息模板](/.image/javafx/消息模板2.png)         | ![消息模板](/.image/javafx/消息模板3.png)         |
| 消息记录     | ![消息记录](/.image/javafx/消息记录1.png)         | ![消息记录](/.image/javafx/消息记录2.png)         | ![消息记录](/.image/javafx/消息记录3.png)         |
| 我的消息     | ![我的消息](/.image/javafx/我的消息1.png)         | ![我的消息](/.image/javafx/我的消息2.png)         | ![我的消息](/.image/javafx/我的消息3.png)         |
| 操作日志     | ![操作日志](/.image/javafx/操作日志1.png)         | ![操作日志](/.image/javafx/操作日志2.png)         | ![操作日志](/.image/javafx/操作日志3.png)         |
| 登录日志     | ![登录日志](/.image/javafx/登录日志1.png)         | ![登录日志](/.image/javafx/登录日志2.png)         | ![登录日志](/.image/javafx/登录日志3.png)         |
| 令牌管理     | ![令牌管理](/.image/javafx/令牌管理1.png)         | ![令牌管理](/.image/javafx/令牌管理2.png)         | ![令牌管理](/.image/javafx/令牌管理3.png)         |
| 文件配置     | ![文件配置](/.image/javafx/文件配置1.png)         | ![文件配置](/.image/javafx/文件配置2.png)         | ![文件配置](/.image/javafx/文件配置3.png)         |
| 文件列表     | ![文件列表](/.image/javafx/文件列表1.png)         | ![文件列表](/.image/javafx/文件列表2.png)         | ![文件列表](/.image/javafx/文件列表3.png)         |
| 定时任务     | ![定时任务](/.image/javafx/定时任务1.png)         | ![定时任务](/.image/javafx/定时任务2.png)         | ![定时任务](/.image/javafx/定时任务3.png)         |
| 执行日志     | ![执行日志](/.image/javafx/执行日志1.png)         | ![执行日志](/.image/javafx/执行日志2.png)         | ![执行日志](/.image/javafx/执行日志3.png)         |
| API-访问日志 | ![API-访问日志](/.image/javafx/API-访问日志1.png) | ![API-访问日志](/.image/javafx/API-访问日志2.png) | ![API-访问日志](/.image/javafx/API-访问日志3.png) |
| API-错误日志 | ![API-错误日志](/.image/javafx/API-错误日志1.png) | ![API-错误日志](/.image/javafx/API-错误日志2.png) | ![API-错误日志](/.image/javafx/API-错误日志3.png) |
| 配置管理     | ![配置管理](/.image/javafx/配置管理1.png)         | ![配置管理](/.image/javafx/配置管理2.png)         | ![配置管理](/.image/javafx/配置管理3.png)         |

### Swing

| 模块       | 浅色                                       | 深色1                                      | 深色2                                      |
|----------|------------------------------------------|------------------------------------------|------------------------------------------|
| 登录       | ![登录](/.image/swing/登录.png)              | -                                        | -                                        |
| 主页       | ![主页](/.image/swing/主页1.png)             | ![主页](/.image/swing/主页2.png)             | ![主页](/.image/swing/主页3.png)             |
| 用户管理     | ![用户管理](/.image/swing/用户管理1.png)         | ![用户管理](/.image/swing/用户管理2.png)         | ![用户管理](/.image/swing/用户管理3.png)         |
| 角色管理     | ![角色管理1](/.image/swing/角色管理1.png)        | ![角色管理2](/.image/swing/角色管理2.png)        | ![用户管理3](/.image/swing/用户管理3.png)        |
| 菜单管理     | ![菜单管理1](/.image/swing/菜单管理1.png)        | ![菜单管理2](/.image/swing/菜单管理2.png)        | ![菜单管理3](/.image/swing/菜单管理3.png)        |
| 部门管理     | ![部门管理](/.image/swing/部门管理1.png)         | ![部门管理](/.image/swing/部门管理2.png)         | ![部门管理](/.image/swing/部门管理3.png)         |
| 岗位管理     | ![岗位管理](/.image/swing/岗位管理1.png)         | ![岗位管理](/.image/swing/岗位管理2.png)         | ![岗位管理](/.image/swing/岗位管理3.png)         |
| 字典管理     | ![字典管理](/.image/swing/字典管理1.png)         | ![字典管理](/.image/swing/字典管理2.png)         | ![字典管理](/.image/swing/字典管理3.png)         |
| 字典数据     | ![字典数据](/.image/swing/字典数据1.png)         | ![字典数据](/.image/swing/字典数据2.png)         | ![字典数据](/.image/swing/字典数据3.png)         |
| 通知公告     | ![通知公告](/.image/swing/通知公告1.png)         | ![字典数据](/.image/swing/通知公告2.png)         | ![字典数据](/.image/swing/通知公告3.png)         |
| 消息模板     | ![消息模板](/.image/swing/消息模板1.png)         | ![消息模板](/.image/swing/消息模板2.png)         | ![消息模板](/.image/swing/消息模板3.png)         |
| 消息记录     | ![消息记录](/.image/swing/消息记录1.png)         | ![消息记录](/.image/swing/消息记录2.png)         | ![消息记录](/.image/swing/消息记录3.png)         |
| 我的消息     | ![我的消息](/.image/swing/我的消息1.png)         | ![我的消息](/.image/swing/我的消息2.png)         | ![我的消息](/.image/swing/我的消息3.png)         |
| 操作日志     | ![操作日志](/.image/swing/操作日志1.png)         | ![操作日志](/.image/swing/操作日志2.png)         | ![操作日志](/.image/swing/操作日志3.png)         |
| 登录日志     | ![登录日志](/.image/swing/登录日志1.png)         | ![登录日志](/.image/swing/登录日志2.png)         | ![登录日志](/.image/swing/登录日志3.png)         |
| 文件配置     | ![文件配置](/.image/swing/文件配置1.png)         | ![文件配置](/.image/swing/文件配置2.png)         | ![文件配置](/.image/swing/文件配置3.png)         |
| 文件列表     | ![文件列表](/.image/swing/文件列表1.png)         | ![文件列表](/.image/swing/文件列表2.png)         | ![文件列表](/.image/swing/文件列表3.png)         |
| 定时任务     | ![定时任务](/.image/swing/定时任务1.png)         | ![定时任务](/.image/swing/定时任务2.png)         | ![定时任务](/.image/swing/定时任务3.png)         |
| 执行日志     | ![执行日志](/.image/swing/执行日志1.png)         | ![执行日志](/.image/swing/执行日志2.png)         | ![执行日志](/.image/swing/执行日志3.png)         |
| API-访问日志 | ![API-访问日志](/.image/swing/API-访问日志1.png) | ![API-访问日志](/.image/swing/API-访问日志2.png) | ![API-访问日志](/.image/swing/API-访问日志3.png) |
| API-错误日志 | ![API-错误日志](/.image/swing/API-错误日志1.png) | ![API-错误日志](/.image/swing/API-错误日志2.png) | ![API-错误日志](/.image/swing/API-错误日志3.png) |
| 配置管理     | ![配置管理](/.image/swing/配置管理1.png)         | ![配置管理](/.image/swing/配置管理2.png)         | ![配置管理](/.image/swing/配置管理3.png)         |

## 许可证

本项目遵循仓库中的 [LICENSE](LICENSE)。
