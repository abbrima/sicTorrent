
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
    private Thread serverThread;

    public void kill() {
        try {
            server.close();
        } catch (IOException ioe) {
        }
    }
    private void createServer(){
        server = null;
        System.out.println("Creating");
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    int n = 0;
                    this.port = 6881;
                    while (n <= 8) {
                        try {
                            server = new ServerSocket(port + n);
                            Info.setPort(port);
                            break;
                        } catch (IOException e) {
                            n++;
                        }
                    }
                    if (n > 8) {
                        throw new IOException();
                    }
                    start();
                    return;
                } catch (IOException ioe) {
                    try{Thread.sleep(30000);}catch(Exception e){

                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public Server() {
        createServer();
    }

    public void start(){
        serverThread = new Thread(this);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void run() {
        while (true) {
            try {
                Socket client = server.accept();
                System.out.println(client.getInetAddress().toString());
                if (!NetworkController.ipExists(client.getInetAddress().getHostAddress()))
                    NetworkController.getConnections().add(new Connection(client));
                else
                    client.close();
            } catch (IOException ioe) {
                createServer();
            }
        }
    }
}