package Responser;

import java.io.OutputStream;

public class C404Responser extends Responser {

    public C404Responser(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public boolean send() throws Exception {
        sendCodeAndText("404 Not Found");
        return true;
    }
}
