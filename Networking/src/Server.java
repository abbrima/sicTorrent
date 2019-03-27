import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    ServerSocket server;
    int port;
    boolean port_works = false;

    public Server() throws IOException{
        int n = 0;
        this.port = 6881;
        while (n <= 8) {
            try {
                server = new ServerSocket(port);
                break;
            } catch (IOException e) {
                n++;
            }
        }
        if (n>8){
            server = new ServerSocket(); port=server.getLocalPort();
        }
        Info.setPort(port);
    }
    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                BufferedReader n = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String s = null;
                while ((s = n.readLine()) != null) {
                    System.out.println(s);
                }

            } catch (Exception e) {
            }
        }
    }
}