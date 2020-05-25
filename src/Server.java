import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[]args) {
        ServerSocket ss=null;

        try {
            ss=new ServerSocket(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                assert ss != null;
                Socket s = ss.accept();
                // System.out.println("catch socket!");
                TaskThread t = new TaskThread(s);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}