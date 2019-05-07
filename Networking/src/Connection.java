import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.stream.Stream;


public class Connection implements Runnable {
    private Socket socket;
    private boolean am_choking = false; //client is choking the peer
    private boolean am_interested = false; //client is interested in peer
    private boolean peer_choking = false;   //peer is chocking client
    private boolean peer_interested = false; // peer is interested


    private ConnectionState state;

    private boolean peerHas[];

    private String debug = "D";
    private String ID;
    private String IP;

    private byte reserved[];

    private Torrent torrent;
    private Thread thread;

    private DataOutputStream ostream;
    private DataInputStream istream;

    private InetAddress address;
    private int port;


    public Connection() {
        state = ConnectionState.INIT;
        ID = "ID";
    }

    public Connection(Socket socket) {
       this();
       this.socket=socket;
       debug = "incoming";
       state = ConnectionState.INCOMING;
       IP = socket.getInetAddress().getHostAddress();
       try{
           istream = new DataInputStream(socket.getInputStream());
           ostream = new DataOutputStream(socket.getOutputStream());

           receiveHandshake();

           if (torrent==null)
           {
               closeSocket(); return;
           }

           sendHandshake(new byte[8]);

           state = ConnectionState.HANDSSHOOK;

           torrent.getConnections().add(this);

           if (torrent.getStatus()!= Torrent.TorrentStatus.FINISHED)
               setInterested(false);
           setChoke(false);

           peerHas = new boolean[torrent.getPieces().size()];

           synchronized(ostream){
               ostream.write(ConnectionMessages.genBitfield(torrent));
           }

           thread = new Thread(this);
           thread.setDaemon(true);
           thread.start();

       }catch(Exception e){
           e.printStackTrace();
           closeSocket();
       }
    }

    public Connection(Torrent torrent, String ip, int port) {
        this();
        peer_interested=true;
        debug = "outgoing";
        IP = ip;
        this.torrent = torrent;
        peerHas = new boolean[torrent.getPieces().size()];
        this.port = port;
        try {
            address = InetAddress.getByName(ip);
            socket = new Socket(address, port);
            ostream = new DataOutputStream(socket.getOutputStream());
            istream = new DataInputStream(socket.getInputStream());

            am_interested = true;
            am_choking = false;

            sendHandshake(new byte[8]);




            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();

        } catch (UnknownHostException uhe) {
            socket = null;
        } catch (IOException ioe) {
            socket = null;
        }
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                if (state == ConnectionState.INIT) {
                    receiveHandshake();
                    if (socket != null && !socket.isClosed()) {
                        state = ConnectionState.HANDSSHOOK;
                        sendBitfield();
                        setInterested(true);
                        setChoke(true);
                    }
                } else {
                    int prefix = istream.readInt();
                    if (prefix == 0) {
                        //keep alive
                    } else {
                        byte id = istream.readByte();
                        switch (id) {
                            case 0: //choke
                                peer_choking = true;
                                break;
                            case 1: //unchoke
                                peer_choking = false;
                                break;
                            case 2: //interested
                                peer_interested = true;
                                break;
                            case 3: //not interested
                                peer_interested = false;
                                break;
                            case 4: //have
                                peerHas[istream.readInt()] = true;
                                break;
                            case 5: //bitfield
                                receiveBitfield(prefix);
                                break;
                            case 6: //request
                                receiveRequest();
                                break;
                            case 7: //piece
                                receivePiece(prefix);
                                break;
                            default:
                                closeSocket();
                                return;
                        }
                    }
                }
                if (peer_choking == false && am_interested == true && getPiecesFromPeer() > 0) {
                    state = ConnectionState.REQUEST;
                    Triplet<Integer, Integer, Integer> request = torrent.createRequest(peerHas, this);

                    if (request != null) {
                        synchronized (ostream) {
                            ostream.write(ConnectionMessages.genRequest(request.getFirst(),
                                    request.getSecond(), request.getThird()));

                        }
                    }
                }
            }
        } catch (IOException ioe) {
            closeSocket();
        }
    }

    public void closeSocket() {
        try {
            if (socket != null)
                socket.close();
        } catch (Exception se) {
        }

    }


    public void sendHandshake(byte[] reserved) throws IOException {
        ostream.write(ConnectionMessages.genHandshake(torrent.getInfoHash(), new byte[8]));
    }

    private void sendBitfield() throws IOException {
        if (torrent.getAvailiblePieces() > 0) {
            synchronized (ostream) {
                ostream.write(ConnectionMessages.genBitfield(torrent));
            }
        }
    }

    public void receiveHandshake() throws IOException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000);
                closeSocket();
            } catch (InterruptedException ie) {
            }
        });
        t.setDaemon(true);
        t.start();
        try {
            byte pstrlen = istream.readByte();
            String pstrn = new String(istream.readNBytes(pstrlen));
            if (!pstrn.equals(ConnectionMessages.protocolString))
                closeSocket();
            reserved = new byte[8];
            istream.read(reserved);
            byte[] hash = new byte[20];
            istream.read(hash);
            if (torrent == null) {
                torrent = NetworkController.checkIfTorrentExists(hash);
                if (torrent == null)
                    if (socket != null)
                        closeSocket();
            } else {
                if (!Arrays.equals(hash, torrent.getInfoHash()))
                    closeSocket();
            }
            byte[] peerID = new byte[20];
            istream.read(peerID);
            ID = new String(peerID);
            t.interrupt();
        } catch (IllegalArgumentException iae) {
            throw new IOException();
        }
        catch(IOException ioe){
            throw ioe;
        }
    }

    private void receiveBitfield(int prefix) throws IOException {
        byte bitfield[] = new byte[prefix - 1];
        istream.readNBytes(bitfield, 0, prefix - 1);
        String bin = Funcs.toBitString(bitfield);
        for (int i = torrent.getPieces().size(); i < (prefix - 1) * 8; i++) {
            if (bin.charAt(i) != '0')
                closeSocket();
        }
        state = ConnectionState.BITFIELD;
        debug = bin;
        char carr[] = bin.toCharArray();
        for (int i = 0; i < torrent.getPieces().size(); i++)
            if (carr[i] == '1')
                peerHas[i] = true;
            else if (carr[i] == '0')
                peerHas[i] = false;
            else
                System.exit(-1);
    }

    private void receiveRequest() throws IOException {
        state = ConnectionState.SENDINGBLOCK;
        int index = istream.readInt();
        int offset = istream.readInt();
        int length = istream.readInt();
        if (index < 0 || index > torrent.getPieces().size())
            closeSocket();
        if (offset + length > torrent.getPieces().get(index).getLength() || offset + length < 0)
            closeSocket();
        if (!am_choking) {
            Piece p = torrent.getPieces().get(index);
            if (p.getStatus() != PieceStatus.HAVE)
                closeSocket();
            else {
                state = ConnectionState.SENDINGBLOCK;
                synchronized (ostream) {
                    try {
                        ostream.write(ConnectionMessages.genBlock(index, offset, p.getBlock(offset, index)));
                    } catch (FileNotFoundException fnfe) {
                        //handle file not found (STUPID USER)
                    }
                }
            }
        }
    }

    private void receivePiece(int prefix) throws IOException {
        int index = istream.readInt();
        int offset = istream.readInt();
        if (index < 0 || index > torrent.getPieces().size())
            closeSocket();
        Piece p = torrent.getPieces().get(index);
        if (prefix - 9 < 0)
            closeSocket();
        byte arr[] = new byte[prefix - 9];
        istream.readNBytes(arr, 0, prefix - 9);
        if (offset + arr.length < 0 || offset + arr.length > p.getLength())
            closeSocket();
        try {
            p.applyBytes(arr, offset, this);
        } catch (FileNotFoundException fnfe) {
            //handle (stupid user)
        }
    }

    public void cancelRequest(Triplet<Integer, Integer, Integer> req) {
        try {
            synchronized (ostream) {
                ostream.write(ConnectionMessages.genCancel(req));
            }
        } catch (IOException ioe) {
        }

    }

    public void sendHave(int index) {
        try {
            synchronized (ostream) {
                ostream.write(ConnectionMessages.genHave(index));
            }
        } catch (Exception e) {
            closeSocket();
        }
    }

    public boolean failed() {
        return socket == null || socket.isClosed();
    }

    public String getID() {
        return ID;
    }

    public ConnectionState getState() {
        return state;
    }

    public String getDebug() {
        return debug;
    }

    public String getIP() {
        return IP;
    }

    private void sendMessage(MessageType type) throws IOException {
        synchronized (ostream) {
            ostream.write(ConnectionMessages.genMessage(type));
        }
    }

    public void setChoke(boolean b) {
        am_choking = b;
        try {
            if (b) {
                sendMessage(MessageType.CHOKE);
            } else {
                sendMessage(MessageType.UNCHOKE);
            }
        } catch (IOException ioe) {
            closeSocket();
        }
    }

    public void setInterested(boolean b) {
        am_interested = b;
        try {
            if (b) {
                sendMessage(MessageType.INTERESTED);
            } else {
                sendMessage(MessageType.NOTINTERESTED);
            }
        } catch (IOException ioe) {
            closeSocket();
        }
    }

    private int getPiecesFromPeer() {
        int n = 0;
        for (int i = 0; i < peerHas.length; i++) {
            if (peerHas[i] == true)
                n++;
        }
        return n;
    }
}
