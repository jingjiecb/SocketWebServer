package Responser;

import java.io.OutputStream;
import java.io.PrintStream;

public class C405Responser extends Responser {

    public C405Responser(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean send() throws Exception {
        try(PrintStream writer = new PrintStream(outputStream)) {
            writer.println("HTTP/1.1 405 Method Not Allowed");
            writer.println("Allow: POST, GET");
            writer.println("Content-Type:text/plain");
            writer.println("Content-Length:22");
            writer.println();
            //发送响应体
            writer.print("405 Method Not Allowed");
            writer.flush();
            return true;
        }
    }
}
