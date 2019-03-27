import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;

enum MessageType
{
    CHOKE, UNCHOKE, INTERESTED, UNINTERESTED, KEEP_ALIVE
}
public class ConnectionMessages {

    boolean am_choking=true; //client is choking the peer
    boolean am_interested=false; //client is interested in peer
    boolean peer_choking=true;   //peer is chocking client
    boolean peer_interested=false; // peer is interested

    public byte[] MakeHandshake(byte [] info_Hash, byte [] reserved )
    {
        byte pstrlen=19;
        String pstr="BitTorrent protocol";
        reserved = new byte[8];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        try
        {
            ostream.writeByte(pstrlen);
            ostream.writeBytes(pstr);
            ostream.write(reserved);
            ostream.write(info_Hash);
            ostream.write(Info.getPeerID().getBytes(StandardCharsets.UTF_8));
        }catch (IOException e){}
        return baos.toByteArray();
    }
    private byte [] MakeMessage(MessageType type) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        switch(type) {
            case KEEP_ALIVE:
                ostream.writeInt(0);
            case CHOKE:
                ostream.writeInt(0);
                ostream.writeByte(1);
            case UNCHOKE:
                ostream.writeInt(1);
                ostream.writeByte(1);
            case INTERESTED:
                ostream.writeInt(1);
                ostream.writeByte(2);
            case UNINTERESTED:
                ostream.writeInt(1);
                ostream.writeByte(3);
        }
        return baos.toByteArray();
    }
    private byte [] MakeHave(int index) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(5);
        ostream.writeByte(4);
        ostream.writeInt(index);
        return baos.toByteArray();
    }
    //private byte [] MakeBitfield()
    private byte [] MakeRequest(int index, int begin, int length) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(13);
        ostream.writeByte(6);
        ostream.writeInt(index);
        ostream.writeInt(begin);
        ostream.writeInt(length);
        return baos.toByteArray();
    }
    private byte [] MakePiece(int index, int begin , byte [] block ) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(9+block.length);
        ostream.writeByte(6);
        ostream.writeInt(index);
        ostream.writeInt(begin);
        ostream.write(block, begin ,block.length);
        return baos.toByteArray();
    }
    private byte [] MakeCancel(int index, int begin, int length) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(13);
        ostream.writeByte(8);
        ostream.writeInt(index);
        ostream.writeInt(begin);
        ostream.writeInt(length);
        return baos.toByteArray();
    }
}
