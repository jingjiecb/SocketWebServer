package Responser;

import java.net.Socket;

public class C500Responser extends Responser {
    Socket socket;

    public C500Responser(Socket socket){
        this.socket=socket;
    }

    @Override
    public boolean send() {
        try {
            outputStream=socket.getOutputStream();
            outputStream.flush();
            sendCodeAndText("500 Internal Server Error");
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
