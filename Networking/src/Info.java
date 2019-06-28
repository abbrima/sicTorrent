import java.util.Random;

public class Info {
    private static String peerID;
    public static int MaxBlockSize=16384;
    public static String getPeerID(){return peerID;}
    private static byte[] nodeID;
    static
    {
        peerID = new String();
        peerID+="-SIC015-";
        Random rand = new Random();
        for (int i=0;i<12;i++)
            peerID+=Integer.toString(rand.nextInt(10));
        nodeID = new byte[20];
        for (int i=0;i<20;i++)
            rand.nextBytes(nodeID);
    }
    private static int port;
    public static void setPort(int p){
        port=p;
    }
    public static int getPort(){return port;}
    public static byte[] getNodeID(){return nodeID;}
}
