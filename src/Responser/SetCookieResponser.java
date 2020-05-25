package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class SetCookieResponser implements Responser {

    private OutputStream outputStream;
    private String cookie;

    public SetCookieResponser(OutputStream outputStream, String cookie) {
        this.outputStream = outputStream;
        this.cookie=cookie;
    }

    @Override
    public boolean send() throws Exception {
        PrintStream writer = new PrintStream(outputStream);
        writer.println("HTTP/1.1 301 Goto");
        writer.println("Set-Cookie: "+cookie);
        writer.println("Location: http://localhost:8888/index.html");
        writer.flush();
        writer.close();
        return true;
    }
}
