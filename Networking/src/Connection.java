import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyPair;

public class Connection {
    ConnectionMessages connect;
  //  Torrent

    public void sendHandshake(String ip, int port, byte [] info_hash)throws IOException {
        try {
            if (port < 1 || port > 65535) {
                throw new Exception("Port is invalid !");
            }
            Socket s = new Socket(ip, port);
            s.setSoTimeout(1000 * 15);
            OutputStream Handshake = s.getOutputStream();
            byte[] R = new byte[8];
            ByteArrayOutputStream request = connect.MakeHandshake(info_hash, R);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(Handshake, request.toString()));
            System.out.println("connection has been made");
        }catch(Exception e){
            System.out.println("CAN'T CONNECT");
        }
    }
    private class receiveHandshake implements Runnable
    {
        ServerSocket server;
        int port;
        boolean port_works= false;
        private void setPort(int port)
        {
            this.port = port;
        }
        public void setport(){
            while (!port_works) {
                try {
                    server = new ServerSocket(6881);
                    System.out.print(server.getLocalPort());
                    port_works = true;

                } catch (Exception e) {
                    setPort(port + 1);
                }
            }
        }
        public void run(){
            while(true)
            {
                try{
                    Socket client = server.accept();
                    BufferedReader n = new BufferedReader( new InputStreamReader( client.getInputStream()));
                    String s = null;
                    while ((s=n.readLine())!=null)
                    {
                        System.out.println(s);
                    }

                }catch (Exception e) {
                }
            }
        }
    }

}
