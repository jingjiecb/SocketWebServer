import Responser.Responser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 可以简单定制：
 * 1. 默认的登陆超时时间为1分钟，这个时间可以在`DAO/CookieDaoImpl.java`中第7行修改。
 * 2. 可以在`DAO/UserDaoImpl.java`中定义更多的初始用户。
 * 3. 可以在`Controller/Controller.java`的27行找到`REDIRECT_MAP`常量，在这里可以添加302重定向规则。只需在括号中加入`put(<原地址>, <重定向地址>);`即可。
 *
 * 运行时可以指定参数：
 * - 如果不带有任何参数运行，则保持默认端口`9000`和默认网站根目录`/home/web`
 * - 如果想自定义网站根目录，请只输入一个参数，是网站的根目录绝对路径。
 * - 如果想自定义网站根目录和端口，请输入两个参数，第一个是网站的根目录，第二个是端口号。
 *
 * 我已经在/resources/中提供了一套可供参考的网站根目录资源，可以简单修改之后使用。
 */
public class Server {
    private static int PORT=9000;

    public static void main(String[]args) {

        try {
            if (args.length == 1) {
                Responser.setBasePath(args[0]);
            } else if (args.length > 1) {
                Responser.setBasePath(args[0]);
                PORT = Integer.parseInt(args[1]);
            }
        }catch (Exception e){
            System.out.println("\033[31m"+"Error | Invalid parameters!\n");
            System.out.println("********** Help information **********");
            System.out.println("If you only want to custom base path, please input ONLY one parameter. DO NOT end with '/' ");
            System.out.println("If you want to custom base path and port, please put base path first and port second.");
            System.out.println("If you don't input any parameters, the default base path is /home/web and the default port is 9000.");
            System.out.println("Make sure your port is free.");
            System.out.println("Try again!"+"\033[0m");
            return;
        }

        ServerSocket ss=null;

        try {
            ss=new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\033[32m"+"**********JAVA SOCKET WEB SERVER**********");
        System.out.println("INFO | Running on port "+PORT);
        System.out.println("INFO | Website base path is: "+Responser.getBasePath());
        System.out.println("******************************************"+"\033[0m");

        while(true) {
            try {
                assert ss != null;
                Socket s = ss.accept();
                // System.out.println("catch socket!");
                TaskThread t = new TaskThread(s);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}