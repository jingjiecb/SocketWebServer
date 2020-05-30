package Controller;

import DAO.CookieDao;
import DAO.CookieDaoImpl;
import DAO.UserDao;
import DAO.UserDaoImpl;
import Parser.Parser;
import Responser.*;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

public class Controller {

    private static final String URL_HOME = "http://js.aegean19.top:9000";

    //请求解析器
    private Parser parser;
    //socket输出流
    private OutputStream outputStream;
    //用户数据访问接口
    private UserDao userDao;
    //Cookie数据访问接口
    private CookieDao cookieDao;

    /**
     * 处理请求
     *
     * @param parser       解析器
     * @param outputStream 输出流
     */
    public void process(Parser parser, OutputStream outputStream) throws Exception {
        this.parser = parser;
        this.outputStream = outputStream;
        userDao = UserDaoImpl.getUserDaoInstance();
        cookieDao = CookieDaoImpl.getCookieDaoInstance();


        String method = parser.getMethod();

        if (method.equals("POST")) {
            processPost();
        } else if (method.equals("GET")) {
            processGet();
        } else {
            new C405Responser(outputStream).send();
        }

    }

    /**
     * 登陆前拦截器
     * 登录前仅允许访问登陆和注册页面
     *
     * @return 如果访问合法返回true，否则返回false
     */
    private boolean interceptBeforeLogin() {
        String path = parser.getPath();
        return path.equals("/login.html") || path.equals("/register.html");
    }

    /**
     * 登陆后拦截器
     * 登录后允许访问根目录下的资源，但是不允许路径中返回上级目录
     *
     * @return 如果访问合法返回true，否则返回false
     */
    private boolean interceptAfterLogin() {
        return !parser.getPath().contains("..");
    }

    /**
     * 重定向
     *
     * @return 如果不需要重定向，返回空串。否则返回重定向地址。
     */
    private String redirect() {
        return "";
    }

    /**
     * POST请求处理
     *
     * @throws Exception 各种异常
     */
    private void processPost() throws Exception {
        String content = parser.getContent();
        String[] pair = content.split("&");

        assert pair.length > 1;

        String userName = (pair[0].split("="))[1];
        String passwd = (pair[1].split("="))[1];

        String path = parser.getPath();

        if (path.equals("/login")) {//如果是来自登陆页面的post
            if (userDao.isMatch(userName, passwd)) {//如果用户密码正确，颁发一个cookie给浏览器
                new SetCookieResponser(outputStream, cookieDao.getNewCookie(userName), URL_HOME).send();
            } else {//如果用户密码错误，则重定向回登陆页面
                new C301Responser(outputStream, URL_HOME + "/login.html").send();
            }
        } else if (path.equals("/register")) {//如果是来自于注册页面的post，添加一个用户
            if (userDao.addUser(userName, passwd)) {
                new C301Responser(outputStream, URL_HOME + "/login.html").send();//注册成功跳转到登陆页面
            } else {
                new C301Responser(outputStream, URL_HOME + "/register.html").send();//注册失败跳转到注册页面
            }
        }
    }

    /**
     * GET请求处理
     *
     * @throws Exception 各种异常
     */
    private void processGet() throws Exception {
        String nameCookie = parser.getCookieByKey("username");

        if (!cookieDao.isValid(nameCookie)) {
            if (!parser.hasCheckModified()) {
                // no 304 check
                if (!interceptBeforeLogin()) new C302Responser(outputStream, URL_HOME + "/login.html").send();
                else sendFile();
            } else {
                // check if 304
                File theFile = new File(parser.getPath());
                Date fileLastModTime = new Date(theFile.lastModified());
                Date clientModSince = parser.getModifiedDate();
                if (fileLastModTime.getTime() > clientModSince.getTime()) {
                    sendFile();
                } else {
                    new C304Responser(outputStream).send();
                }
            }
        } else {
            if (!interceptAfterLogin()) new C404Responser(outputStream);
            else {
                String redirectPath = redirect();
                if (redirectPath.equals("")) sendFile();
                else new C302Responser(outputStream, redirectPath).send();
            }
        }
    }

    /**
     * 发送文件内容
     *
     * @throws Exception 各种异常
     */
    private void sendFile() throws Exception {
        String path = parser.getPath();
        if (!new MIMEResponser(outputStream, path).send()) {//如果文件不存在，发送404应答
            System.out.println("info ===> 404: " + path);
            new C404Responser(outputStream).send();
        }
    }

}
