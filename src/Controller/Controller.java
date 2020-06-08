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
import java.util.HashMap;

public class Controller {
    private static final String[] PASS_PATTERNS_BEFORE_LOGIN = {
            "/login.html",
            "/register.html",
            ".*?.css",
            ".*?.ico"
    };

    private static final String[] NO_PASS_PATTERNS_AFTER_LOGIN = {
            ".*?(\\.\\.).*"
    };

    private static final HashMap<String, String> REDIRECT_MAP = new HashMap<String, String>() {
        {
            put("/", "/index.html");
            put("/baidu", "https://www.baidu.com");
            put("/music.mp3", "/test.mp3");
        }
    };

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
     * @param request      输入请求字符数组
     * @param outputStream 输出流
     */
    public void process(char[] request, OutputStream outputStream) throws Exception {
        parser = new Parser(request);
        this.outputStream = outputStream;
        userDao = UserDaoImpl.getUserDaoInstance();
        cookieDao = CookieDaoImpl.getCookieDaoInstance();

        parser.print();

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
     * POST请求处理
     *
     * @throws Exception 各种异常
     */
    private void processPost() throws Exception {
        String path = parser.getPath();

        switch (path) {
            case "/login": //如果是来自登陆页面的post
                login();// 处理登陆

                break;
            case "/register": //如果是来自于注册页面的post
                register();// 处理注册

                break;
            case "/logout": //如果是登出请求
                logout();//处理登出

                break;
        }
    }

    /**
     * 处理登陆
     */
    private void login() throws Exception {
        try {
            String content = parser.getContent();
            String[] pairs = content.split("&");

            String[] userNameInfo = pairs[0].split("=");
            String[] passwdInfo = pairs[1].split("=");

            String userName = userNameInfo[1];
            String passwd = passwdInfo[1];

            if (userDao.isMatch(userName, passwd)) {//如果用户密码正确，颁发一个cookie给浏览器
                new SetCookieResponser(outputStream, cookieDao.getNewCookie(userName)).send();
                System.out.println("\033[31;4m" + "info ===> user login: " + userName + "\033[0m");
            } else {//如果用户密码错误，则重定向回登陆页面
                new C302Responser(outputStream, "/login.html").send();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            new C302Responser(outputStream, "/login.html").send();
        }
    }

    /**
     * 处理注册
     */
    private void register() throws Exception {
        try {
            String content = parser.getContent();
            String[] pairs = content.split("&");
            assert pairs.length == 3;
            String userName = (pairs[0].split("="))[1];
            String passwd = (pairs[1].split("="))[1];
            String passwdRe = (pairs[2].split("="))[1];

            if (passwd.equals(passwdRe) && userDao.addUser(userName, passwd)) {
                new C301Responser(outputStream, "/login.html").send();//注册成功跳转到登陆页面
                System.out.println("\033[31;4m" + "info ===> new user register: " + userName + "\033[0m");
            } else {
                new C302Responser(outputStream, "/register.html").send();//两次输入密码不一致，或者已有重复用户名存在，不予注册，弹回注册页面重新输入信息
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            new C302Responser(outputStream, "/register.html").send();
        }
    }

    /**
     * 处理登出
     */
    private void logout() throws Exception {
        String cookie = parser.getCookie();
        cookieDao.disableCookie(cookie);
        new C301Responser(outputStream, "/login.html").send();//成功删除cookie，则回到登录界面
        System.out.println("\033[31;4m" + "info ===> logout: " + cookie + "\033[0m");
    }

    /**
     * GET请求处理
     *
     * @throws Exception 各种异常
     */
    private void processGet() throws Exception {
        // 拦截
        if (intercept()) return;
        // 重定向
        if (redirect()) return;
        // 304
        if (check304()) return;
        // 筛选全部通过后发送文件
        sendFile();

    }

    /**
     * 检查是否需要拦截请求
     *
     * @return 如果被拦截了，返回true；没被拦截，返回false
     */
    private boolean intercept() throws Exception {
        String nameCookie = parser.getCookieByKey("username");

        if (!cookieDao.isValid(nameCookie)) {// 请求无有效cookie
            // 按照登录前进行处理
            return interceptBeforeLogin();
        } else {// 请求有有效cookie
            return interceptAfterLogin();
        }

    }

    /**
     * 登陆前拦截器
     * 登录前仅允许访问登陆和注册页面
     *
     * @return 如果访问合法返回false，否则返回false并重定向回登陆页面
     */
    private boolean interceptBeforeLogin() throws Exception {
        String path = parser.getPath();

        // 如果匹配到允许访问资源的正则，返回true
        for (String passPattern : PASS_PATTERNS_BEFORE_LOGIN) {
            if (path.matches(passPattern)) return false;
        }

        // 否则为非法访问，重定向为登录页
        new C302Responser(outputStream, "/login.html").send();
        System.out.println("\033[31;4m" + "info ===> intercept before login: " + path + "\033[0m");
        return true;
    }

    /**
     * 登陆后拦截器
     * 登录后允许访问根目录下的资源，但是不允许路径中返回上级目录
     *
     * @return 如果访问合法返回false，否则返回true并重定向主页
     */
    private boolean interceptAfterLogin() throws Exception {
        String path = parser.getPath();

        // 如果匹配到不允许访问的页面正则，那么重定向回主页
        for (String noPassPattern : NO_PASS_PATTERNS_AFTER_LOGIN) {
            if (path.matches(noPassPattern)) {
                new C301Responser(outputStream, "/index.html").send();
                System.out.println("\033[31;4m" + "info ===> intercept after login: " + path + "\033[0m");
                return true;
            }
        }

        // 没有匹配到非法页面，返回true
        return false;
    }

    /**
     * 重定向
     *
     * @return 如果不需要重定向，返回空串。否则返回重定向地址。
     */
    private boolean redirect() throws Exception {
        String path = parser.getPath();
        String redirectPath = REDIRECT_MAP.get(path);

        if (redirectPath != null) {
            new C302Responser(outputStream, redirectPath).send();
            System.out.println("\033[31;4m" + "info ===> redirect: from " + path + " to " + redirectPath + "\033[0m");
            return true;
        }

        return false;
    }


    /**
     * 检查是否可以返回304
     *
     * @return 如果可以返回304，直接发回304状态报文并返回true；如果不能返回304，则返回false
     */
    private boolean check304() throws Exception {
        // 304 检查
        if (!parser.hasCheckModified()) {
            // no 304 check
            return false;
        } else {
            // check if 304
            File theFile = new File(parser.getPath());
            Date fileLastModTime = new Date(theFile.lastModified());
            Date clientModSince = parser.getModifiedDate();
            if (fileLastModTime.getTime() > clientModSince.getTime()) {
                return false;
            } else {
                new C304Responser(outputStream).send();
                System.out.println("\033[31;4m" + "info ===> 304: " + theFile + "\033[0m");
                return true;
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
            System.out.println("\033[31;4m" + "info ===> 404: " + path + "\033[0m");
            new C404Responser(outputStream).send();
        }
    }

}
