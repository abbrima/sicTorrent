import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;

public class Connection implements Runnable {
    private Socket socket;
    private boolean am_choking = false; //client is choking the peer
    private boolean am_interested = false; //client is interested in peer
    private boolean peer_choking = false;   //peer is chocking client
    private boolean peer_interested = false; // peer is interested
    private Torrent torrent;
    private DataOutputStream ostream;
    private DataInputStream istream;
    private Thread connectionThread;
    private boolean kill;
    private InetAddress address;
    private int port;
    public boolean dead(){return kill;}

    public void run() {
        if (socket==null)
        try {
            socket = new Socket(address, port);
            ostream = new DataOutputStream(socket.getOutputStream());
            istream = new DataInputStream(socket.getInputStream());
            sendHandshake(null);
            try{receiveHandShake();}catch(IOException ioee){ioee.printStackTrace();}
        } catch (IOException ioe) {
            kill = true;
            return;
        }
    }

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        ostream = new DataOutputStream(socket.getOutputStream());
        istream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public Connection(Torrent torrent, String ip, int port) throws UnknownHostException {
        address = InetAddress.getByName(ip);
        this.port=port;
        this.torrent = torrent;
    }

    public void sendHandshake(byte reserved[]) throws IOException {
        try {
            ostream.write(ConnectionMessages.makeHandshake(torrent.getInfoHash(), reserved));
            System.out.println("connection has been made");
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    public void receiveHandShake() throws IOException {
        try{Thread.sleep(10000);}catch(Exception e){}
       int length = istream.readByte();
       byte arr[] = new byte[length];
       istream.read(arr);
       System.out.println(new String(arr, StandardCharsets.UTF_8));
       byte reserved[] = new byte[8];
       istream.read(reserved);
       byte infohash[] = new byte[20],peerID[] = new byte[20];
       istream.read(infohash);
       istream.read(peerID);
       if (Arrays.equals(infohash,torrent.getInfoHash()))
           System.out.println("HANDSHAKE SUCCESSFUL");
    }

    public void start() {
        kill = false;
        connectionThread = new Thread(this);
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    public void kill() {
        kill = true;
        try{socket.close();}catch(Exception e){}
        connectionThread.interrupt();
    }


}
