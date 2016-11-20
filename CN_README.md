#  简诗

一款优雅的中国风Android App，包括Android端和Server端，支持登录注册，数据云端同步，离线数据存储和截屏分享等功能。

<a href="https://play.google.com/store/apps/details?id=com.wingjay.android.jianshi"><img alt="Google Play" src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height=50px/></a>

[下载地址: Fir.im](http://fir.im/vd1r)



# 技术
### Android

简诗 Android 依赖了当前最流行的 Android 库来搭建稳定简洁的架构。

- 数据库层:           `DBFlow` to manage sqlite database;
- 网络层:            `OkHttp3` & `Retrofit2`, and `Stetho` to debug http requests;
- Rx层:                 `RxJava` & `RxAndroid`;
- 依赖注入:  `Dagger2`;
- Logging:            `Timber`;
- 图片加载:      `Picasso`;
- 崩溃监测:      `Crashlytics`;
- 其它:              `Eventbus`.

感谢 [`Square`](https://github.com/square) 为我们提供了很多优秀的开源库.

### 服务端

简诗服务端主要采用了 Python 和 Flask.

服务端主要分成三层：

- www层：该层主要用来接受client传过来的请求，解析参数，解密token和对所有request记录logging等，然后调用logic层来进行处理；
- logic层：该层主要用来处理业务相关的逻辑，并且调用db层的接口来进行数据操作；
- db层：该层将所有数据库操作封装起来，执行db操作，并对外提供抽象的数据处理接口。


# Android 界面

<img src="material/screenshots/with_phone/1.PNG" width = 400>
<img src="material/screenshots/with_phone/2.PNG" width = 400>
<img src="material/screenshots/with_phone/3.PNG" width = 400>
<img src="material/screenshots/with_phone/4.PNG" width = 400>
<img src="material/screenshots/with_phone/7.PNG" width = 400>
<img src="material/screenshots/with_phone/8.PNG" width = 400>

<img src="material/screenshots/with_phone/6.png" width = 400>
<img src="material/screenshots/3.png" width = 400>

<img src="material/screenshots/with_phone/5.jpg" width = 600>
<img src="material/screenshots/shareImage.png" width = 800>


# 中文博客介绍
[如何在一天之内完成一款具备cool属性的Android产品_简书](http://www.jianshu.com/p/cf496fc408b2)


# 贡献者
我们一起为 简诗 搭建 Android 和 Server：[wingjay](https://github.com/wingjay), [RayPan](https://github.com/Panl). 

# 开发历史 
项目的开发都是利用本人的业余时间，因此进度并没有太快。

- __[2016/11/06] 配置阿里云服务器；安装Nginx， gunicorn等运行环境；搭建jenkins进行server部署；编写Unit Test__
- __[2016/10/30] 注册登录界面的设计，截屏分享等其它比较酷的小功能。__
- __[2016/10/25] 完成 Android 和后台的数据同步，将数据同步至后台；提高后台的错误处理能力和config机制。 [Server+Android]__
- __[2016/09/03] 完成 Android 端的用户管理，注册和登录流程。 [Server+Android]__
- __[2016/08/25] 服务器从 `LeanCloud` 迁移本地到 `Apache + Mysql + wsgi + Flask` 的结构上，自行搭建 Mysql 数据库。 [Server]__
- __[2016/08/20] 着手搭建`python` `Flask`后台框架，并基于 `LeanCloud` 来进行部署。 [Server]__
- __[2016/08/18] 在Android端集成 `Dagger` `Retrofit2` `OkHttp3` `RxJava`等库。 [Android]__

# 参考
[《Producter》](http://producter.io/)

# License
Apache
