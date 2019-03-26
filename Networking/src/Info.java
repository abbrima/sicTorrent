import java.util.Random;

public class Info {
    private static String peerID;
    public static int MaxBlockSize=16384;
    public static String getPeerID(){return peerID;}
    public static void initPeerID(){
        peerID = new String();
        peerID+="-SIC002-";
        Random rand = new Random();
        for (int i=0;i<12;i++)
            peerID+=Integer.toString(rand.nextInt(10));
    }
    private static int port;
    public static void initPort(){
        Random rand = new Random();
        port = rand.nextInt(9)+6881;
    }
    public static int getPort(){return port;}

}
