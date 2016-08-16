## 项目介绍

简诗 是一个包括移动端(Android)与服务端的完整项目，目前由我一人维护开发。



当前目录为 简诗 的服务端代码，部署环境为Apache2.4 和 MySQL5.7，开发框架为Flask，开发语言为Python。



## 开发日志：

1. 2016/08/15: 采用LeanCloud搭建环境，能够从在线服务器返回json结果。



2. 2016/08/16: 由于LeanCloud对于数据库操作限制较多，未来决定采用自建服务器和数据库的方式来继续。在本地Mac OS X搭建环境 Apache + MySQL + WSGI module。目前已经能够在本地访问 http://jay.local/get ，直接通过wsgi进入到flask的环境中，并接受读取json返回结果。
