package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class SetCookieResponser extends Responser {

    private String cookie;
    private String baseUrl;

    public SetCookieResponser(OutputStream outputStream, String cookie,String baseUrl) {
        this.outputStream = outputStream;
        this.cookie=cookie;
        this.baseUrl=baseUrl;
    }

    @Override
    public boolean send() throws Exception {
        PrintStream writer = new PrintStream(outputStream);
        writer.println("HTTP/1.1 301 Move Permanently");
        writer.println("Set-Cookie: "+cookie);
        writer.println("Location: "+baseUrl+"/index.html");
        writer.flush();
        writer.close();
        return true;
    }
}
