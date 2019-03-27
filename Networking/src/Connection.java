import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyPair;

public class Connection {
    private boolean am_choking=true; //client is choking the peer
    private boolean am_interested=false; //client is interested in peer
    private boolean peer_choking=true;   //peer is chocking client
    private boolean peer_interested=false; // peer is interested
  //  Torrent
    public Connection(){
    }
    public void sendHandshake(String ip, int port, byte [] info_hash)throws IOException {
        try {
            if (port < 1 || port > 65535) {
                throw new Exception("Port is invalid !"+port);
            }
            InetAddress address = InetAddress.getByName(ip);
            Socket s = new Socket(address, port);
            s.setSoTimeout(100 * 15);
            DataOutputStream ostream = new DataOutputStream(s.getOutputStream());
            byte[] reserved = new byte[8];
            ostream.write(ConnectionMessages.makeHandshake(info_hash, reserved));
            System.out.println("connection has been made");
        }catch(Exception e){
            e.printStackTrace();
            //System.out.println("CAN'T CONNECT");
        }
    }


}
