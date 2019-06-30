
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

public class Server implements Runnable {
    ServerSocket server;
    int port;

    public void kill()
    {
        try {
            server.close();
        } catch (IOException ioe) {
        }
    }

    public Server() throws IOException
    {
        int n = 0;
        this.port = 6881;
        while (n <= 8) {
            try {
                server = new ServerSocket(port+n);
                Info.setPort(port);
                break;
            } catch (IOException e) {
                n++;
            }
        }
        if (n > 8) {
            throw new IOException();
        }
    }

    public void run()
    {
        while (true) {
            try {
                Socket client = server.accept();
                System.out.println(client.getInetAddress().toString());
                if (!NetworkController.ipExists(client.getInetAddress().getHostAddress()))
                    NetworkController.getConnections().add(new Connection(client));
                else
                    client.close();
            } catch (IOException ioe) {
                return;
            }
        }
    }
}