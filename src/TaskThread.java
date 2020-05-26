import Controller.Controller;
import Parser.Parser;
import Responser.*;

import java.io.*;
import java.net.Socket;


public class TaskThread extends Thread {

    private Socket s;

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

            //处理请求，给出应答
            Controller controller=new Controller();
            controller.process(parser,outputStream);

        } catch (Exception e) {
            e.printStackTrace();
            new C500Responser(s).send();
        }

    }

}
