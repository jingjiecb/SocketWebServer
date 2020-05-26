package Responser;

import java.io.*;

public class MIMEResponser extends Responser {

    private String path;


    public MIMEResponser(OutputStream outputStream, String path) {
        this.outputStream=outputStream;
        this.path=path;
    }

    /**
     * 发送MIME类型相应报文
     * @return 文件存在，发送成功则返回true。文件不存在则返回false
     * @throws Exception 各种异常
     */
    @Override
    public boolean send() throws Exception{

        File file = new File(path);

        if (file.exists()) {

            PrintStream writer = new PrintStream(outputStream);
            DataOutputStream os = new DataOutputStream(outputStream);
            //发送响应头
            writer.println("HTTP/1.1 200 OK");
            if (path.endsWith(".html") || path.endsWith(".htm")) {
                writer.println("Content-Type:text/html");
            } else if (path.endsWith(".jpg")) {
                writer.println("Content-Type:image/jpeg");
            } else if (path.endsWith(".css")) {
                writer.println("Content-Type:text/css");
            } else {
                writer.println("Content-Type:application/octet-stream");
            }

            FileInputStream in = new FileInputStream(path);
            writer.println("Content-Lenth:" + in.available());
            writer.println();
            writer.flush();

            //发送响应体
            byte[] b = new byte[1024];
            int len = 0;
            len = in.read(b);
            while (len != -1) {
                os.write(b, 0, len);
                len = in.read(b);
            }

            os.flush();
            os.close();
            writer.close();
            return true;
        }
        else {
            return false;
        }
    }
}
