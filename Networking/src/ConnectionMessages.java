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

    public static byte[] makeHandshake(byte [] info_Hash, byte [] reserved )
    {
        byte pstrlen=19;
        String pstr=new String("BitTorrent protocol");
        reserved = new byte[8];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        try
        {
            ostream.writeByte(pstrlen);
            ostream.write(pstr.getBytes(StandardCharsets.UTF_8));
            ostream.writeByte(0);
            ostream.write(info_Hash);
            ostream.write(Info.getPeerID().getBytes(StandardCharsets.UTF_8));
        }catch (IOException e){}
        return baos.toByteArray();
    }
    public static byte [] MakeMessage(MessageType type) throws IOException
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
    public static byte [] MakeHave(int index) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(5);
        ostream.writeByte(4);
        ostream.writeInt(index);
        return baos.toByteArray();
    }
    //private byte [] MakeBitfield()
    public static byte [] MakeRequest(int index, int begin, int length) throws IOException
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
    public static byte [] MakePiece(int index, int begin , byte [] block ) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream ostream = new DataOutputStream(baos);
        ostream.writeInt(9+block.length);
        ostream.writeByte(6);
        ostream.writeInt(index);
        ostream.writeInt(begin);
        ostream.write(block, begin ,block.length);
        return baos.toByteArray();
    }
    public static byte [] MakeCancel(int index, int begin, int length) throws IOException{
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
