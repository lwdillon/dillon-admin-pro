# Dillon-Admin-Pro

java权限管理系统桌面客户端

## 简介

本项目是一套权限管理系统的用户界面(UI)实现，分别使用java图形化JavaFX和Java Swing技术实现了两套桌面客户端界面，同时也可支持web的界面使用

以下是本项目使用的技术栈和相关组件：

主题：使用 atlantafx 主题库，该主题库提供了一套现代化、响应式的用户界面风格，帮助美化系统的外观和用户体验。

图标库：使用 ikonli 图标库，该图标库提供了丰富的矢量图标集合，可用于系统的图标显示和按钮等元素的装饰。

动画库：采用 AnimateFX 动画库，该库提供了多种动画效果，可以为系统的界面元素添加各种动态效果，增强用户体验。

HTTP库：Retrofit结合rxjava3封装一下调用rest请求调用，用于与后端服务器进行通信和数据交互。

本项目的目标是提供一套完整的、易于使用的权限管理系统UI，适用于个人用户和企业用户。界面设计参考了若依前端基于 [芋道源码/yudao-ui-admin-vue3](https://github.com/yudaocode/yudao-ui-admin-vue3/)
的设计，以提供现代化、直观的用户界面。同时，使用了多种技术和组件来增强用户体验，包括数据绑定、主题化、图标和动画等方面。

* 界面基于 [芋道源码/yudao-ui-admin-vue3](https://github.com/yudaocode/yudao-ui-admin-vue3/)
* 后端采用[芋道源码/ruoyi-vue-pro](https://github.com/YunaiV/ruoyi-vue-pro)
* 前端技术栈:
    * javafx主题 [atlantafx](https://github.com/mkpaz/atlantafx) ([文档](https://mkpaz.github.io/atlantafx/))
    * javafx图标库 [ikonli](https://github.com/kordamp/ikonli) ([文档](https://kordamp.org/ikonli/))
    * jafafx动画库 [AnimateFX](https://github.com/Typhon0/AnimateFX) ([文档](https://github.com/Typhon0/AnimateFX/wiki))
    * http客户端库 [Forest](https://forest.kim/)
    * javaSwing主题 [FlatLaf](https://https://www.formdev.com/flatlaf/) 



## 启动说明

## 快速启动（后端项目）

### 1 初始化 Redis

项目使用 Redis 缓存数据，所以需要启动一个 Redis 服务。
> 不会安装的胖友，可以选择阅读下文
> * Windows 安装 Redis 指南：http://www.iocoder.cn/Redis/windows-install
> * Mac 安装 Redis 指南：http://www.iocoder.cn/Redis/mac-install

默认配置下，Redis 启动在 6379 端口，不设置账号密码。如果不一致，需要修改 application-local.yaml 配置文件。

```agsl
  # Redis 配置。Redisson 默认的配置足够使用，一般不需要进行调优
  redis:
    host: 127.0.0.1 # 地址
    port: 6379 # 端口
    database: 0 # 数据库索引
```

### 2 启动后端项目

> 执行 com.dillon.lw.server.DillonServerApplication 类，进行启动。

启动完成后，使用浏览器访问 http://127.0.0.1:48080地址，返回如下 JSON 字符串，说明成功。

```agsl
{
    "code": 401,
    "data": null,
    "msg": "账号未登录"
}

```

## javafx

```agsl
    1 运行主类com.dillon.lw.fx.AppStart即可
```

## javaSwing

```agsl
    1 运行主类com.dillon.lw.DillonSwingUiApplication即可
```

> 友情提示：本项目基于 芋道源码/ruoyi-vue-pro <https://gitee.com/zhijiantianya/ruoyi-vue-pro> 修改
>
> * 后端只修改了system_menu表，增加了component_swing和component_fx两个字段，分别对应javaSwing和javaFx的界面类

## 🐶 新手必读

* 演示地址【Vue3 + element-plus】：<http://dashboard-vue3.yudao.iocoder.cn>
* 启动文档：<https://doc.iocoder.cn/quick-start/>
* 视频教程：<https://doc.iocoder.cn/video/>

### 系统功能

|    | 功能   | 描述                              |
|----|------|---------------------------------|
|    | 用户管理 | 用户是系统操作者，该功能主要完成系统用户配置          |
| ⭐️ | 在线用户 | 当前系统中活跃用户状态监控，支持手动踢下线           |
|    | 角色管理 | 角色菜单权限分配、设置角色按机构进行数据范围权限划分      |
|    | 菜单管理 | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能    |
|    | 部门管理 | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限  |
|    | 岗位管理 | 配置系统用户所属担任职务                    |
|    | 字典管理 | 对系统中经常使用的一些较为固定的数据进行维护          |
| 🚀 | 操作日志 | 系统正常操作日志记录和查询，集成 Swagger 生成日志内容 |
| ⭐️ | 登录日志 | 系统登录日志记录查询，包含登录异常               |
|    | 通知公告 | 系统通知公告信息发布维护                    |

## 🐷 演示图

### 系统功能

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

### Java Swing

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
