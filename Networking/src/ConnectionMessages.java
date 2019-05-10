import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

enum MessageType{
    CHOKE,KEEPALIVE,UNCHOKE,INTERESTED,NOTINTERESTED,
}

class ConnectionMessages{
    static String protocolString = "BitTorrent protocol";
    public static byte[] genHandshake(byte[] infoHash,byte[] reserved) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeByte(19);
        os.writeBytes(protocolString);
        os.write(reserved);
        os.write(infoHash);
        os.writeBytes(Info.getPeerID());
        return baos.toByteArray();
    }
    public static byte[] genMessage(MessageType type) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        switch(type){
            case CHOKE:
                os.writeInt(1);
                os.writeByte(0);
                break;
            case UNCHOKE:
                os.writeInt(1);
                os.writeByte(1);
                break;
            case INTERESTED:
                os.writeInt(1);
                os.writeByte(2);
                break;
            case NOTINTERESTED:
                os.writeInt(1);
                os.writeByte(3);
                break;
            case KEEPALIVE:
                os.writeInt(0);
                break;
        }
        return baos.toByteArray();
    }
    public static byte[] genBitfield(Torrent torrent)throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);

        String binaryString = new String();
        for (Piece p:torrent.getPieces()){
            if (p.getStatus()==PieceStatus.HAVE)
                binaryString+="1";
            else
                binaryString+="0";
        }
        while(binaryString.length()%8!=0)
            binaryString+="0";
        byte arr[] = Funcs.getByteByString(binaryString);

        os.writeInt(1+arr.length);
        os.writeByte(5);
        os.write(arr);


        return baos.toByteArray();
    }
    public static byte[] genBlock(int index,int offset,byte[] data) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeInt(9+data.length);
        os.writeByte(7);
        os.writeInt(index);
        os.writeInt(offset);
        os.write(data);
        return baos.toByteArray();
    }
    public static byte[] genRequest(int index,int offset,int length) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeInt(13);
        os.writeByte(6);
        os.writeInt(index);
        os.writeInt(offset);
        os.writeInt(length);
        return baos.toByteArray();
    }
    public static byte[] genCancel(Triplet<Integer,Integer,Integer> req)throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);

        os.writeInt(13);
        os.writeByte(8);
        os.writeInt(req.getFirst());
        os.writeInt(req.getSecond());
        os.writeInt(req.getThird());

        return baos.toByteArray();
    }
    public static byte[] genHave(int index) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);

        os.writeInt(5);os.writeByte(4);os.writeInt(index);

        return baos.toByteArray();
    }
}