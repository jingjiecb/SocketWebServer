import DAO.*;
import Parser.Parser;
import Responser.*;

import java.io.*;
import java.net.Socket;


public class TaskThread extends Thread {

    private static final String BASEPATH = "D:/2019-2020学年第二学期/课程材料/互联网计算/大作业/web";

    private Socket s;
    Responser responser;
    UserDao userDao = UserDaoImpl.getUserDaoInstance();
    CookieDao cookieDao = CookieDaoImpl.getCookieDaoInstance();


    public TaskThread(Socket s) {
        this.s = s;
    }

    public void run() {
        BufferedReader reader;
        char[] postContent;

        try {
            OutputStream outputStream = s.getOutputStream();
            //读取请求
            postContent = new char[1024];
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            int co = reader.read(postContent);
            assert co > 0;

            //解析请求
            Parser parser = new Parser(postContent);
            parser.print();


            if (parser.getMethod() == 1) {
                String content = parser.getContent();
                String[] pair = content.split("&");
                String userName = (pair[0].split("="))[1];
                String passwd = (pair[1].split("="))[1];

                String path=parser.getPath();

                if (path.equals("/login")) {
                    if (userDao.isMatch(userName, passwd)) {
                        responser = new SetCookieResponser(outputStream, cookieDao.getNewCookie(userName));
                        responser.send();
                    } else {
                        responser = new C301Responser(outputStream, "http://localhost:8888/login.html");
                        responser.send();
                    }
                } else if (path.equals("/register")){
                    userDao.addUser(userName,passwd);
                    responser = new C301Responser(outputStream, "http://localhost:8888/login.html");
                    responser.send();
                }

            } else {
                String rawPath = parser.getPath();

                String nameCookie = parser.getCookieByKey("username");
                System.out.println(nameCookie);
                if (!cookieDao.isValid(nameCookie) &&
                        !rawPath.equals("/login.html") &&
                        !rawPath.equals("/register.html")) {
                    responser = new C301Responser(outputStream, "http://localhost:8888/login.html");
                    responser.send();
                } else {

                    //尝试发送文件

                    String path = BASEPATH + rawPath;
                    responser = new MIMEResponser(outputStream, path);
                    if (!responser.send()) {//如果文件不存在，发送404应答
                        responser = new C404Responser(outputStream);
                        responser.send();
                    }

                    s.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
