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
    3、在resource下创建mongofile文件夹，该文件夹为被扫描主文件夹。下面再创建要执行操作的propertifile文件即可  
二、基于创建对象的执行方式：  
    1、创建AutomaticDriver对象，调用init方法传入uri或ip+port  
    2、在resource下创建mongofile文件夹，该文件夹为被扫描主文件夹。下面再创建要执行操作的propertifile文件即可  
三、注意事项：  
    1、文件夹层级关系如下（支持多数据库同时操作，角标为01，02。。。因为直接对比的字符串）  
    ![微信图片1](https://user-images.githubusercontent.com/59118548/156518673-bef92e33-23a4-4d42-af4c-2cd34e198516.png)
    2、操作文件需如下配置（条件没有可为空，针对字段的操作一律选择document）  
    ![1644285077(1)](https://user-images.githubusercontent.com/59118548/152902982-339e9297-2a23-4ad5-b60a-8596b35180b1.jpg)



