package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class C301Responser extends Responser {

    private String url;

    public C301Responser(OutputStream outputStream, String url) {
        this.outputStream = outputStream;
        this.url=url;
    }

    @Override
    public boolean send() throws Exception {
        try(PrintStream writer = new PrintStream(outputStream)) {
            writer.println("HTTP/1.1 301 Move Permanently");
            writer.println("Location: " + url);
            writer.flush();
            return true;
        }
    }
}
