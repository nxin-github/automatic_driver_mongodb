# automatic_driver_mongodb

#### 介绍
一个关于mongodb的自动版本控制工具，只需要在resource/mongofile下创建简单的配置文件便可轻松执行mongodb操作。灰常的方便

#### 软件架构
软件架构说明


#### 安装教程

直接引入jar包就行，虽然我现在还没传到中央仓库

#### 使用说明

一、基于spring的自动执行方式：  
    1、添加com.liang到项目扫描路径  
    2、配置uri：spring.data.mongodb.uri  
    3、在resource下创建mongofile文件夹，该文件夹为被扫描主文件夹。下面再创建以数据库名为命名的子文件夹，下面再创建要执行操作的propertifile文件即可  
二、基于创建对象的执行方式：  
    1、创建AutomaticDriver对象，调用init方法传入uri或ip+port  
    2、在resource下创建mongofile文件夹，该文件夹为被扫描主文件夹。下面再创建以数据库名为命名的子文件夹，下面再创建要执行操作的propertifile文件即可  
三、注意事项：  
    1、文件夹层级关系如下（支持多数据库同时操作）  
    ![输入图片说明](https://images.gitee.com/uploads/images/2022/0116/222157_f751560f_7686322.png "微信图片1.png")  
    2、操作文件需如下配置（条件没有可为空，针对字段的操作一律选择document）\n
    ![输入图片说明](https://images.gitee.com/uploads/images/2022/0116/222418_0f9e6d01_7686322.png "微信图片3.png")  

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
