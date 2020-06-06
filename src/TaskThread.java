import Controller.Controller;
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
        char[] request;

        try {
            OutputStream outputStream = s.getOutputStream();

            //读取请求
            request = new char[1024];
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            int co = reader.read(request);
            assert co > 0;

            //处理请求，给出应答
            new Controller().process(request,outputStream);

            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            new C500Responser(s).send();
        }

    }

}
