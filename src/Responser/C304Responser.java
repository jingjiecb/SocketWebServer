package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class C304Responser extends Responser {

    public C304Responser(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean send() throws Exception {
        try(PrintStream writer = new PrintStream(outputStream)) {
            writer.println("HTTP/1.1 304 NotModified");
            // tell the client/browser that it can use its buffer
            writer.flush();
            return true;
        }
    }
}
