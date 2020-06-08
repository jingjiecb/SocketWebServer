package Responser;

import java.io.*;
import java.util.HashMap;

public class MIMEResponser extends Responser {
    private static HashMap<String, String> mimeTable = new HashMap<String, String>() {
        {
            put(".html", "Content-Type:text/html");
            put(".htm", "Content-Type:text/html");
            put(".jpg","Content-Type:image/jpeg");
            put(".css","Content-Type:text/css");
            put(".mp4","Content-Type:video/mpeg4");
            put(".png","Content-Type:image/png");
            put(".pdf","Content-Type:application/pdf");
            put(".zip","Content-Type:application/zip");
            put(".mp3","Content-Type:audio/mp3");
        }
    };

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

            try (PrintStream writer = new PrintStream(outputStream); DataOutputStream os = new DataOutputStream(outputStream); FileInputStream in = new FileInputStream(path)) {
                //发送响应头
                writer.println("HTTP/1.1 200 OK");
                writer.println(getMimeType());

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
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 得到MIME类型的描述
     *
     * @return MIME类型描述
     */
    private String getMimeType() {
        String res = "Content-Type:application/octet-stream";

        for (String fileType : mimeTable.keySet()){
            if (path.endsWith(fileType)){
                res=mimeTable.get(fileType);
                break;
            }
        }

        return res;
    }

}
