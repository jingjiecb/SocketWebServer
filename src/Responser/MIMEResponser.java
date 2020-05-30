package Responser;

import java.io.*;

public class MIMEResponser extends Responser {

    private String path;

    public MIMEResponser(OutputStream outputStream, String path) {
        this.outputStream = outputStream;
        this.path = BASEPATH + path;
    }

    /**
     * 发送MIME类型相应报文
     *
     * @return 文件存在，发送成功则返回true。文件不存在则返回false
     * @throws Exception 各种异常
     */
    @Override
    public boolean send() throws Exception {

        File file = new File(path);

        if (file.exists()) {

            PrintStream writer = new PrintStream(outputStream);
            DataOutputStream os = new DataOutputStream(outputStream);
            //发送响应头
            writer.println("HTTP/1.1 200 OK");
            writer.println(getMimeType());

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
        } else {
            return false;
        }
    }

    /**
     * 得到MIME类型的描述
     * @return MIME类型描述
     */
    private String getMimeType() {
        String res = "";

        if (path.endsWith(".html") || path.endsWith(".htm")) {
            res = "Content-Type:text/html";
        } else if (path.endsWith(".jpg")) {
            res = "Content-Type:image/jpeg";
        } else if (path.endsWith(".css")) {
            res = "Content-Type:text/css";
        } else if (path.endsWith(".mp4")) {
            res = "Content-Type:video/mpeg4";
        } else if (path.endsWith(".png")) {
            res = "Content-Type:image/png";
        } else if (path.endsWith(".pdf")) {
            res = "Content-Type:application/pdf";
        } else if (path.endsWith(".zip")) {
            res = "Content-Type:application/zip";
        } else if (path.endsWith(".mp3")) {
            res = "Content-Type:audio/mp3";
        } else {
            res = "Content-Type:application/octet-stream";
        }

        return res;
    }

}
