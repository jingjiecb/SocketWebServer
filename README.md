# Java Socket API编写简单的web服务器

## 功能

经过测试，这个简单的web服务器可以完成如下的功能：

- 用户登陆
- 用户注册
- 下载多种MIME类型的资源
- 可以处理请求并根据情况发送200、301、302、304、404、405、500七种状态码
- 支持html文本、plaintext、jpeg、mpeg4、png、pdf、zip七种MIME类型数据的发送。

## 特点

纯粹使用`Java Socket API`进行编写，不使用任何相关框架。

## 部署说明

### Step1 准备根目录

确保你的网站根目录下有如下三个文件：

1. login.html
2. register.html
3. index.html

我已经在resources/中提供了一个参考版本，你可以简单修改之后使用，也可以编写自己的，但是有以下2点必须注意：

1. 将login.html中的表单(form)的action属性改为`你的服务器地址:端口号+/login`，将register.html中表单的action属性改为`你的服务器地址:端口号+/register`
2. 将所有url修改为你的url。

### Step2 简单修改代码

**！！首先必须要改的位置**有：

1. Controller包的`Controller.java`中第16行，将`URL_HOME`常量改为你的URL，以确保一些内置的跳转正常。
2. 将Responser包中`Responser.java`中第16行，将`BASEPATH`常量改为你的网站根目录。

**可选的更改**有：

1. 为了避免冲突，默认端口是`9000`。如果需要更改，需要将`Server.java`中第10行中的端口改掉。

2. Controller包的`Controller.java`中第78行，有如下方法：

   ```java
   private String redirect() {
       return "";
   }
   ```

   可以在这个方法中定义一些临时的资源重定向（即页面跳转），例如

   ```java
   private String redirect() {
       if (path.equals("/old.html")) return URL_HOME+"/new.html";
       else if(path.equals("/baidu")) return "www.baidu.com";
       else return "";
   }
   ```

3. 默认的登陆超时时间为1分钟，这个时间可以在`DAO/CookieDaoImpl.java`中第7行修改。

4. 可以在`DAO/UserDaoImpl.java`中定义更多的初始用户。

### Step3 打包并运行！

建议打成jar包后上传到有jre环境的服务器，就可以运行了！

## 项目结构

下图是一个简单的类关系示意图：

![ServerSocketClasses](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/SocketServer.png)

具体的**工作流程**如下：

1. Server监听9000默认端口，一旦有连接则创建一个TaskThread对象，并把ServerSocket.accept()方法返回的Socket对象传给这个新线程。
2. TaskThread线程对象从Socket的输入流中读出完整的Http请求报文，并以此为参数构造Parser对象，Parser对象可以处理请求，用几个简单的方法获得请求中有价值的内容。
3. TaskThread实例化一个Controller对象，并将Socket的输出流和Parser对象的引用交给它。
4. Controller首先判断请求的方法（这里只处理POST和GET两种），如果方法非法就实例化C405Responser对象发送405状态应答报文。如果方法合法，则会根据方法选择交给processGet()或者processPost()方法。
5. 在processPost()中，如果是登陆请求，调用UserDao的接口方法查询卡密是否正确，如果正确就颁发一个Cookie给来者；否则弹回登陆页面。如果是注册请求，调用UserDao的addUser方法添加用户信息。
6. 在processGet()中，首先判断请求是否需要被拦截（没有Cookie的资源访问请求和企图访问上级目录的请求将被拦截），拦截后对页面重定向。然后判断是否匹配设定好的资源重定向记录（通过redirect方法），如果不需要重定向，则尝试将请求的内容取出并应答。请求的资源不存在时会产生404应答，资源存在时则根据资源的类型指定MIME类型，并发回应答报文。
7. 一切异常会抛出到TaskServer处理，处理的方式为尝试发回500应答报文。

其中UserDao和CookieDao都使用单件模式，确保不会出现被多次创建造成逻辑错误。

## 展示

登陆页面，输入正确的用户名和密码后方可登陆，获得服务端的Cookie，默认一分钟后失效。

![login](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/login.jpg)

注册页面，输入用户名和密码后注册(其中确认输入密码并无卵用qwq)：

![register](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/register.jpg)

登陆成功后跳转到首页，这里呈现了一些资源：

![index](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/index.jpg)

点击图片时，因为有MIME类型声明，所以浏览器会默认查看图片而不是下载：

![jpg](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/jpg.jpg)

可以下载音乐、视频、压缩包、电子书资源：

![mp3](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/mp3.jpg)

## 存在的问题

不得不说，这还是个很简陋的web服务器，存在很多风险和问题，具体如下：

1. 没有对注册用户的第二次输入密码进行校验。
2. 没有验证码等任何措施阻止恶意的频繁登陆和注册请求。
3. **不能处理带有中文的请求。**可能需要处理一下编码相关问题。
4. 偶尔会出现输入输出流的异常，而且很多时候无法利索地处理。如果信息已经发送中途发生异常，再发送500应答很可能造成文件的损坏，和一系列其他问题。
5. 发送文件时没有告知接受者文件的大小，导致接收方不能预估文件下载时间。这一点应该比较好修复。
6. 不支持续传。
7. 可能存在容易被攻击者利用的漏洞。

如果你有什么好的想法可以帮助改进，欢迎在issue中提出！

## 展望

除了可以解决上面提到的问题，这个项目还有很大的补充完善空间。下面是一些我能想到的完善角度：

1. 支持更多的MIME类型和状态码
2. 支持自动根据根目录资源来生成主页页面
3. 支持嵌套目录结构的浏览
4. ...待补充

所以这其实只是一个互联网课程的大作业qwq