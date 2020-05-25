import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class TaskThread extends Thread {
    private Socket s;

    public TaskThread(Socket s) throws IOException {
        this.s = s;
    }

    public void run() {
        BufferedReader reader;
        PrintStream writer;
        FileInputStream in;
        DataOutputStream os;
        String firstLineOfRequest;

        try {
            //读取请求
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String line;
            char[] postContent = new char[100];
            do {
                line = reader.readLine();
                System.out.println(line);
            } while (!line.equals(""));
            int co = reader.read(postContent);
            assert co>0;
            System.out.println(postContent);
            System.out.println("****************");

            firstLineOfRequest = reader.readLine();
            String uri = firstLineOfRequest.split(" ")[1];

            //读文件，写应答
            writer = new PrintStream(s.getOutputStream());
            File file = new File("D:/2019-2020学年第二学期/课程材料/互联网计算/大作业/web" + uri);
            if (file.exists()) {
                writer.println("HTTP/1.1 200 OK");//返回应答消息，并结束应答
                if (uri.endsWith(".html")) {
                    writer.println("Content-Type:text/html");
                } else if (uri.endsWith(".jpg")) {
                    writer.println("Content-Type:image/jpeg");
                } else if (uri.endsWith(".css")) {
                    writer.println("Content-Type:text/css");
                } else {
                    writer.println("Content-Type:application/octet-stream");
                }
                in = new FileInputStream("D:/2019-2020学年第二学期/课程材料/互联网计算/大作业/web" + uri);
                //发送响应头
                writer.println("Content-Lenth:" + in.available());
                writer.println();
                writer.flush();
                //发送响应体
                os = new DataOutputStream(s.getOutputStream());
                byte[] b = new byte[1024];
                int len = 0;
                len = in.read(b);
                while (len != -1) {
                    os.write(b, 0, len);
                    len = in.read(b);
                }
                os.flush();
                writer.close();
            } else {
                //发送响应头
                writer.println("HTTP/1.1 404 Not Found");
                writer.println("Content-Type:text/plain");
                writer.println("Content-Length:7");
                writer.println();
                //发送响应体
                writer.print("访问内容不存在");
                writer.flush();
                writer.close();
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
