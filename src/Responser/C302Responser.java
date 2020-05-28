package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class C302Responser extends Responser {

    private String url;

    public C302Responser(OutputStream outputStream, String url) {
        this.outputStream = outputStream;
        this.url=url;
    }

    @Override
    public boolean send() throws Exception {
        PrintStream writer = new PrintStream(outputStream);
        writer.println("HTTP/1.1 302 Move Temporarily");
        writer.println("Location: " + url);
        writer.flush();
        writer.close();
        return true;
    }
}
