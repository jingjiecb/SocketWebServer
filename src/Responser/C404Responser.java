package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class C404Responser implements Responser {

    private OutputStream outputStream;

    public C404Responser(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean send() throws Exception {
        PrintStream writer = new PrintStream(outputStream);
        writer.println("HTTP/1.1 404 Not Found");
        writer.println("Content-Type:text/plain");
        writer.println("Content-Length:13");
        writer.println();
        //发送响应体
        writer.print("no such thing");
        writer.flush();
        writer.close();
        return true;
    }
}
