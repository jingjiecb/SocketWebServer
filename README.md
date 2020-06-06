# Java Socket API编写简单的web服务器

## 功能

经过测试，这个简单的web服务器可以完成如下的功能：

- 用户登陆
- 用户注册
- 可以处理请求并根据情况发送200、301、302、304、404、405、500七种状态码
- 支持html文本、plaintext、jpeg、mpeg4、png、pdf、zip七种MIME类型数据的发送。

## 特点

纯粹使用`Java Socket API`进行编写，不使用任何相关框架。

## 部署说明

### 部署方法一：源代码打包构建

**简单修改源代码（可选）**：

1. 默认的登陆超时时间为1分钟，这个时间可以在`DAO/CookieDaoImpl.java`中第7行修改。
5. 可以在`DAO/UserDaoImpl.java`中定义更多的初始用户。
3. 可以在`Controller/Controller.java`的27行找到`REDIRECT_MAP`常量，在这里可以添加302重定向规则。只需在括号中加入`put(<原地址>, <重定向地址>);`即可。

建议打成jar包运行。

运行时可以**指定参数**：

- 如果不带有任何参数运行，则保持默认端口`9000`和默认网站根目录`/home/web`
- 如果想自定义网站根目录，请只输入一个参数，是网站的根目录绝对路径。
- 如果想自定义网站根目录和端口，请输入两个参数，第一个是网站的根目录，第二个是端口号。

**准备网站根目录**

确保你的网站根目录下有如下三个文件：

1. login.html
2. register.html
3. index.html

我已经在resources/中提供了一个参考版本，你可以简单修改之后使用。

### 部署方法二：docker构建

我已经制作好了docker镜像，包含一个最新的jar包和我的示例网站资源，可以一键拉取运行：

```
docker pull registry.cn-hangzhou.aliyuncs.com/claws/socket_server:3.0 && docker run -d -p 9000:9000 registry.cn-hangzhou.aliyuncs.com/claws/socket_server:3.0
```

之后就可以访问本机的9000端口看到效果。

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