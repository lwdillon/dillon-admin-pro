

# Dillon-Admin-Pro

java权限管理系统桌面客户端

## 简介

本项目是一套权限管理系统的用户界面(UI)实现，分别使用java图形化JavaFX和Java Swing技术实现了两套桌面客户端界面，同时也可支持web的界面使用


以下是本项目使用的技术栈和相关组件：

应用程序结构：采用 mvvmFX 框架，该框架基于 MVVM (Model-View-ViewModel) 架构模式，用于实现数据绑定和视图模型的管理。

主题：使用 atlantafx 主题库，该主题库提供了一套现代化、响应式的用户界面风格，帮助美化系统的外观和用户体验。

组件库：初期采用 MaterialFX 组件库，但目前暂时不使用，因为作者正在进行重构工作。组件库用于提供常见的界面组件和交互元素，以简化开发过程。

图标库：使用 ikonli 图标库，该图标库提供了丰富的矢量图标集合，可用于系统的图标显示和按钮等元素的装饰。

动画库：采用 AnimateFX 动画库，该库提供了多种动画效果，可以为系统的界面元素添加各种动态效果，增强用户体验。

HTTP库：使用 OpenFeign HTTP库，该库提供了方便的 HTTP 请求和响应处理功能，用于与后端服务器进行通信和数据交互。

本项目的目标是提供一套完整的、易于使用的权限管理系统UI，适用于个人用户和企业用户。界面设计参考了若依前端基于 RuoYi-Vue
的设计，以提供现代化、直观的用户界面。同时，使用了多种技术和组件来增强用户体验，包括数据绑定、主题化、图标和动画等方面。

请注意，本项目仅提供用户界面(UI)部分的实现，后端采用了若依/RuoYi-Cloud 框架作为后台支持。如果需要完整的权限管理系统，需要结合后端框架使用。

* 界面参考若依前端(基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue))
* 后端采用[若依/RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud)
* 前端技术栈:
    * 应用程序结构 [mvvmFX](https://github.com/sialcasa/mvvmFX) ([文档](https://github.com/sialcasa/mvvmFX/wiki))
    * 主题 [atlantafx](https://github.com/mkpaz/atlantafx) ([文档](https://mkpaz.github.io/atlantafx/))
    * 组件库 [MaterialFX](https://github.com/palexdev/MaterialFX)（组件都是用原生的，暂时不用MaterialFX，等待作者重构完成！）
    * 图标库 [ikonli](https://github.com/kordamp/ikonli) ([文档](https://kordamp.org/ikonli/))
    * 动画库 [AnimateFX](https://github.com/Typhon0/AnimateFX) ([文档](https://github.com/Typhon0/AnimateFX/wiki))
    * http库 [OpenFeign](https://github.com/OpenFeign/feign)

## mvvmfx包找不到的话请使用Sonatype快照存储库，将此存储库添加到 pom.xml 的 <repositorys> 部分
```agsl
<repository>
		<id>sonatype-snapshots</id>
		<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
</repository>
```
## 启动说明

## javafx
```agsl
    1 在idea右侧栏找到Maven，展开并点击Plugins->sass-cli:run，会编译出index.css
    2 运行主类com.lw.fx.AppStart即可
```
## javaSwing
```agsl
    1 运行主类com.lw.swing.DillonSwingUiApplication即可
```

> 友情提示：本项目基于 芋道源码/ruoyi-vue-pro <https://gitee.com/zhijiantianya/ruoyi-vue-pro> 修改
>
> * 后端只修改了system_menu表，增加了component_swing和component_fx两个字段，分别对应javaSwing和javaFx的界面类


## 🐶 新手必读

* 演示地址【Vue3 + element-plus】：<http://dashboard-vue3.yudao.iocoder.cn>
* 启动文档：<https://doc.iocoder.cn/quick-start/>
* 视频教程：<https://doc.iocoder.cn/video/>

### 系统功能

|     | 功能    | 描述                              |
|-----|-------|---------------------------------|
|     | 用户管理  | 用户是系统操作者，该功能主要完成系统用户配置          |
| ⭐️  | 在线用户  | 当前系统中活跃用户状态监控，支持手动踢下线           |
|     | 角色管理  | 角色菜单权限分配、设置角色按机构进行数据范围权限划分      |
|     | 菜单管理  | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能    |
|     | 部门管理  | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限  |
|     | 岗位管理  | 配置系统用户所属担任职务                    |
|     | 字典管理  | 对系统中经常使用的一些较为固定的数据进行维护          |
| 🚀  | 操作日志  | 系统正常操作日志记录和查询，集成 Swagger 生成日志内容 |
| ⭐️  | 登录日志  | 系统登录日志记录查询，包含登录异常               |
|     | 通知公告  | 系统通知公告信息发布维护                    |

## 🐷 演示图

### 系统功能

### JavaFX
| 模块   | 浅色                                 | 深色                                 | 玻璃                                 |
|------|------------------------------------|------------------------------------|------------------------------------|
| 登录   | ![登录](/.image/javafx/登录.png)       | -                                  | -                                  |
| 主页   | ![主页](/.image/javafx/主页1.png)      | ![主页](/.image/javafx/主页2.png)      | ![主页](/.image/javafx/主页3.png)      | 
| 主页2  | ![主页2](/.image/javafx/主页21.png)    | ![主页2](/.image/javafx/主页22.png)    | ![主页2](/.image/javafx/主页23.png)    | 
| 用户管理 | ![用户管理](/.image/javafx/用户管理1.png)  | ![用户管理](/.image/javafx/用户管理2.png)  | ![用户管理](/.image/javafx/用户管理3.png)  | 
| 角色管理 | ![角色管理1](/.image/javafx/角色管理1.png) | ![角色管理2](/.image/javafx/角色管理2.png) | ![用户管理3](/.image/javafx/用户管理3.png) | 
| 菜单管理 | ![菜单管理1](/.image/javafx/菜单管理1.png) | ![菜单管理2](/.image/javafx/菜单管理2.png) | ![菜单管理3](/.image/javafx/菜单管理3.png) | 
| 部门管理 | ![部门管理](/.image/javafx/部门管理1.png)  | ![部门管理](/.image/javafx/部门管理2.png)  | ![部门管理](/.image/javafx/部门管理3.png)  | 
| 岗位管理 | ![岗位管理](/.image/javafx/岗位管理1.png)  | ![岗位管理](/.image/javafx/岗位管理2.png)  | ![岗位管理](/.image/javafx/岗位管理3.png)  | 
| 字典管理 | ![字典管理](/.image/javafx/字典管理1.png)  | ![字典管理](/.image/javafx/字典管理2.png)  | ![字典管理](/.image/javafx/字典管理3.png)  | 
| 字典数据 | ![字典数据](/.image/javafx/字典数据1.png)  | ![字典数据](/.image/javafx/字典数据2.png)  | ![字典数据](/.image/javafx/字典数据3.png)  | 
| 通知公告 | ![通知公告](/.image/javafx/通知公告1.png)  | ![通知公告](/.image/javafx/通知公告2.png)  | ![通知公告](/.image/javafx/通知公告3.png)  | 
| 操作日志 | ![操作日志](/.image/javafx/操作日志1.png)  | ![操作日志](/.image/javafx/操作日志2.png)  | ![操作日志](/.image/javafx/操作日志3.png)  | 
| 登录日志 | ![登录日志](/.image/javafx/登录日志1.png)  | ![登录日志](/.image/javafx/登录日志2.png)  | ![登录日志](/.image/javafx/登录日志3.png)  | 
| 令牌管理 | ![令牌管理](/.image/javafx/令牌管理1.png)  | ![令牌管理](/.image/javafx/令牌管理2.png)  | ![令牌管理](/.image/javafx/令牌管理3.png)  | 


### Java Swing
| 模块   | 浅色                                | 深色                                | 玻璃                                |
|------|-----------------------------------|-----------------------------------|-----------------------------------|
| 登录   | ![登录](/.image/swing/登录.png)       | -                                 | -                                 |
| 用户管理 | ![用户管理](/.image/swing/用户管理1.png)  | ![用户管理](/.image/swing/用户管理2.png)  | ![用户管理](/.image/swing/用户管理3.png)  | 
| 角色管理 | ![角色管理1](/.image/swing/角色管理1.png) | ![角色管理2](/.image/swing/角色管理2.png) | ![用户管理3](/.image/swing/用户管理3.png) | 
| 菜单管理 | ![菜单管理1](/.image/swing/菜单管理1.png) | ![菜单管理2](/.image/swing/菜单管理2.png) | ![菜单管理3](/.image/swing/菜单管理3.png) | 
| 部门管理 | ![部门管理](/.image/swing/部门管理1.png)  | ![部门管理](/.image/swing/部门管理2.png)  | ![部门管理](/.image/swing/部门管理3.png)  | 
| 岗位管理 | ![岗位管理](/.image/swing/岗位管理1.png)  | ![岗位管理](/.image/swing/岗位管理2.png)  | ![岗位管理](/.image/swing/岗位管理3.png)  | 
| 字典管理 | ![字典管理](/.image/swing/字典管理1.png)  | ![字典管理](/.image/swing/字典管理2.png)  | ![字典管理](/.image/swing/字典管理3.png)  | 
| 字典数据 | ![字典数据](/.image/swing/字典数据1.png)  | ![字典数据](/.image/swing/字典数据2.png)  | ![字典数据](/.image/swing/字典数据3.png)  | 
| 通知公告 | ![通知公告](/.image/swing/通知公告1.png)  | ![字典数据](/.image/swing/通知公告2.png)  | ![字典数据](/.image/swing/通知公告3.png)  | 
| 操作日志 | ![操作日志](/.image/swing/操作日志1.png)  | ![操作日志](/.image/swing/操作日志2.png)  | ![操作日志](/.image/swing/操作日志3.png)  | 
| 登录日志 | ![登录日志](/.image/swing/登录日志1.png)  | ![登录日志](/.image/swing/登录日志2.png)  | ![登录日志](/.image/swing/登录日志3.png)  | 

