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
            DataOutputStream ostream = new DataOutputStream(s.getOutputStream());
            byte[] reserved = new byte[8];
            byte[] request = connect.MakeHandshake(info_hash, reserved);
            ostream.write(request);
            System.out.println("connection has been made");
        }catch(Exception e){
            System.out.println("CAN'T CONNECT");
        }
    }


}
