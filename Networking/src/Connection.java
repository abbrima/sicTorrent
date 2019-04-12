import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;

public class Connection {
    boolean failed;
    private Socket socket;
    private boolean am_choking = false; //client is choking the peer
    private boolean am_interested = false; //client is interested in peer
    private boolean peer_choking = false;   //peer is chocking client
    private boolean peer_interested = false; // peer is interested
    private boolean active;
    private byte [] peerHas;
    private byte [] peerWants;
    private Torrent torrent;
    private DataOutputStream ostream;
    private DataInputStream istream;
    private Thread connectionThread;
    private boolean kill;
    private InetAddress address;
    private int port;
    public boolean dead(){return kill;}



    public Connection(Socket socket) throws IOException
    {
        this.socket = socket;
        ostream = new DataOutputStream(socket.getOutputStream());
        istream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public Connection(Torrent torrent, String ip, int port) throws UnknownHostException
    {
        address = InetAddress.getByName(ip);
        this.port=port;
        this.torrent = torrent;
        am_interested = true;
        peer_choking =true;
    }
    public boolean hasFailed()
    {
        return failed;
    }
    public Thread sending()
    {
        class t implements Runnable{
            public synchronized void run() {
                try {
                    sendHandshake(new byte [8]);
                    while (true)
                    {
                        if(peerHas != null)
                        {
                            sendRequest();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("CONNECTION FAILED");

                    // e.printStackTrace();
                }
            }

        }             Thread th = new Thread(new t());
        return th;
    }
    public Thread receiving()
    {
        class t implements Runnable{
            public synchronized void run() {
                try {
                    if(hasFailed()){
                        return;
                    }
                    while (true)
                    {
                        if(!active)
                            receiveHandShake();
                        else {
                            System.out.println("\n"+peerHas[1]+" "+peerHas[2]+"\n");
                            if(peerHas != null)
                        {
                            System.out.println("\nSending request\n");
                            sendRequest();
                        }
                            System.out.println("Waiting for msg");
                            int prefix=istream.readInt();
                            System.out.print("\nprefix:"+prefix+"\n");
                            if (prefix <= 1)
                                receiveMessage(prefix);
                            if (prefix == 5)
                                receiveHave();
                            if (prefix == 13)
                                receiveRequest();
                            else if(prefix==287)
                                receiveBitfield();
                        }
                    }
                } catch (Exception e) {
                    //  System.out.println("CONNECTION FAILED");
                    // e.printStackTrace();
                }
            }

       }             Thread th = new Thread(new t());
        return th;
    }
    public void sendHandshake(byte reserved[]) throws IOException
    {
                try {
                    socket = new Socket(address, port);
                    ostream = new DataOutputStream(socket.getOutputStream());
                    istream = new DataInputStream(socket.getInputStream());
                    ostream.write(ConnectionMessages.makeHandshake(torrent.getInfoHash(), reserved));
                    System.out.println("connection has been made");
                    am_interested=true;
                    failed = false;
                } catch (Exception e) {
                    failed = true;
                    System.out.println("CONNECTION FAILED");
                    // e.printStackTrace();
                }

    }

    public void receiveHandShake() throws IOException
    {
        if(hasFailed()){
            return;
        }
        else {
                try {
                    Thread.sleep(10000);
                    int length = istream.readByte();
                    byte arr[] = new byte[length];
                    istream.read(arr);
                    byte reserved[] = new byte[8];
                    istream.read(reserved);
                    byte infohash[] = new byte[20], peerID[] = new byte[20];
                    istream.read(infohash);
                    istream.read(peerID);
                    if (Arrays.equals(infohash, torrent.getInfoHash())) {
                        System.out.println("HANDSHAKE SUCCESSFUL");
                        peer_interested = true;
                        active = true;
                        failed = false;
                    } else if (!Arrays.equals(infohash, torrent.getInfoHash())) {
                        failed = true;
                        active = false;
                    }
                } catch (Exception e) {
                }


        }
    }
    public void sendMessage(MessageType type){
        try{
            ostream.write(ConnectionMessages.MakeMessage(MessageType.INTERESTED));
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
    }
    public void receiveMessage(int prefix){

                    try {
                        Thread.sleep(10000);
                        byte readID ;
                        int ID;
                        if (prefix==0000) {
                            //keep alive
                        }
                        else if (prefix ==0001) {
                            readID = istream.readByte();
                            ID = readID;
                            if(ID == 0)
                                peer_choking = true;
                            if( ID == 1)
                                peer_choking = false;
                            if( ID == 2)
                                peer_interested = true;
                            if( ID == 3)
                                peer_interested = false;

                            System.out.println("\nMessage Received"+ID+"\n");
                        }
                       else{
                            System.out.println("UNSUCCESSFUL");
                        }
                    } catch (Exception e) {
                }

    }
    public void sendHave(){}
    public void receiveHave(){

            try {
                Thread.sleep(10000);
                System.out.println("Succuessful have");
                byte  readID = istream.readByte();
                int index = istream.readInt();
                System.out.println(index);
                peerHas[index]=1;

            } catch (Exception e) {
            }


    }
    public void sendRequest() throws IOException {

        boolean made = false;
        int i=0;
        while (!made)
        {
            if (peerHas[i] == 1)
            {
                ostream.write(ConnectionMessages.MakeRequest(i,0,16384));
                System.out.println("\nRequest Has been sent\n");
                made = true;
            }
            i++;
        }
    }
    public void receiveRequest(){
        while (true) {
            try {
                Thread.sleep(10000);
                //read
                byte  readID = istream.readByte();
                int index = istream.readInt();
                int offset = istream.readInt();
               int length = istream.readInt();
               //change
                peerWants[index]=1;

            } catch (Exception e) {
            }

        }
    }
    public static String toBinaryString(byte b []) {

        char[] bits = new char[8 * b.length];
        for(int i = 0; i < b.length; i++) {

            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for(int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if(bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }
    public void sendBitfield(){}
    public void receiveBitfield(){
        try {
            //read
            byte readID = istream.readByte();
            byte [] bitfield = new byte [286];
            istream.read(bitfield);
            String a = toBinaryString(bitfield);
            System.out.println("\nbitfield size is :"+a.length()+"\n");
            for (int i=0; i<a.length(); i++) {
                peerHas[i]= a.charAt(i)=='1' ? (byte)1 : (byte)0;
            }

        } catch (Exception e) {
        }
    }
    public void sendPiece(){}
    public void receivepiece() {
        while (true) {
            try {
                Thread.sleep(10000);
                //read//9+block.length
                byte  ID = istream.readByte();
                int index = istream.readInt();
                int offset = istream.readInt();
                int length = istream.readInt();
                byte [] block = new byte [length];
                 istream.read(block,0,length);
                //change
                //save block

            } catch (Exception e) {
            }

        }
    }
    public void kill()
    {
        kill = true;
        try{socket.close();}catch(Exception e){}
        connectionThread.interrupt();
    }

}
