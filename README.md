# Java Socket API编写简单的web服务器

## 功能

经过测试，这个简单的web服务器可以完成如下的功能：

- 用户登陆
- 用户注册
- 用户登出
- 可以处理请求并根据情况发送200、301、302、304、404、405、500七种状态码
- 支持html文本、plaintext、jpeg、mpeg4、png、pdf、zip七种MIME类型数据的发送。

## 特点

纯粹使用`Java Socket API`进行编写，不使用任何相关框架。

## Quick Start

### Docker

我已经制作好了docker镜像，包含一个最新的jar包和我的示例网站资源，可以一键拉取运行：

```
docker pull registry.cn-hangzhou.aliyuncs.com/claws/socket_server:4.0 && docker run -d -p 9000:9000 registry.cn-hangzhou.aliyuncs.com/claws/socket_server:4.0
```

之后就可以访问本机的9000端口看到效果。

### jar包

已在release中提供现成的jar包可供下载。如果你想定制自己的服务器，也可以对源代码进行简单修改定制：

1. 默认的登陆超时时间为1分钟，这个时间可以在`DAO/CookieDaoImpl.java`中第7行修改。
5. 可以在`DAO/UserDaoImpl.java`中定义更多的初始用户。
3. 可以在`Controller/Controller.java`的27行找到`REDIRECT_MAP`常量，在这里可以添加302重定向规则。只需在括号中加入`put(<原地址>, <重定向地址>);`即可。

运行时可以**指定参数**：

- 如果不带有任何参数运行，则保持默认端口`9000`和默认网站根目录`/home/web`
- 如果想自定义网站根目录，请只输入一个参数，是网站的根目录绝对路径。
- 如果想自定义网站根目录和端口，请输入两个参数，第一个是网站的根目录，第二个是端口号。

**准备网站根目录**

确保你的网站根目录下至少有如下三个文件：

1. login.html
2. register.html
3. index.html

我已经在resources/中提供了一套可供参考的网站根目录资源，你可以简单修改之后使用。

## 项目结构

下图是一个简单的类关系示意图：

![ServerSocketClasses](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/SocketServer.png)

大致的**工作流程**如下：

1. Server监听9000默认端口，一旦有连接则创建一个TaskThread对象，并把ServerSocket.accept()方法返回的Socket对象传给这个新线程。
2. TaskThread线程对象从Socket的输入流中读出完整的Http请求报文，实例化Controller处理请求。
3. Controller中实例化Parser，负责提取请求中的有用信息。
4. Controller判断请求的方法（这里只处理POST和GET两种），并交给processGet和processPost方法处理。
5. processPost实现对用户登录、注册、登出的处理。
6. processGet实现拦截、重定向请求，并会根据请求头做出发送403还是具体文件内容的判断。

## 展示

登陆页面。可以输入用户密码登陆，也可以点击注册跳转到注册页面。

![登陆](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/1.jpg)

注册页面。可以输入用户名和两次密码注册，也可以点击登陆回到登陆页面。

![注册](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/2.jpg)

资源页面。也可以浏览资源，也可以点击登出，退出登陆。

![资源](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/3.jpg)

图片资源。服务器可以正常地处理图片资源，浏览器默认显示而不是下载。

![图片](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/4.jpg)

音乐资源。这类资源IE中默认下载。

![音乐](https://clwasblog-1301107071.cos.ap-shanghai.myqcloud.com/img/StudyNote/internet/SocketServer/5.jpg)存在的问题

不得不说，这还是个很简陋的web服务器，存在一些风险和问题：

3. **不能处理带有中文的请求。**可能需要处理一下编码相关问题。
4. 对于浏览器重置连接的行为无法正常处理，只能抛出异常。
5. 发送文件时不告知接受者文件的大小。
4. ...

如果你有什么好的想法可以帮助改进，欢迎在issue中提出！

## 展望

除了可以解决上面提到的问题，这个项目还有很大的补充完善空间，例如：

1. 支持更多的MIME类型和状态码
2. 支持自动根据根目录资源来生成主页页面
3. 支持嵌套目录结构的浏览
4. ...
