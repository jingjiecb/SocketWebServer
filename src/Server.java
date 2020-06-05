import Responser.Responser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
            System.out.println("Error | Invalid parameters!");
            System.out.println("Help information:");
            System.out.println("If you only want to custom base path, please input ONLY one parameter. DO NOT end with '/' ");
            System.out.println("If you want to custom base path and port, please put base path first and port second.");
            System.out.println("If you don't input any parameters, the default base path is /home/web and the default port is 9000.");
            System.out.println("Try again!");
            return;
        }

        ServerSocket ss=null;

        try {
            ss=new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("**********JAVA SOCKET WEB SERVER**********");
        System.out.println("Info | running on port "+PORT);
        System.out.println("Info | Website base path is: "+Responser.getBasePath());
        System.out.println("******************************************");

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