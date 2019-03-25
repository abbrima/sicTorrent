import java.util.ArrayList;

public class Piece {
    private int length;
    private int downloaded;
    private byte hash[];
    private PieceStatus status;
    public PieceStatus getStatus(){return status;}
    public int getDownloaded(){return downloaded;}
    public int getLength(){return length;}
    private ArrayList<DataLocation> blockTable;

    public Piece(int length,byte hash[]){
        this.length=length;
        this.hash=hash;
        status=PieceStatus.UNFINISHED;
        blockTable=new ArrayList<>();
    }
    public byte[] getBlock(int offset,int size){

        return null;
    }
    public void applyBytes(byte[] bytes,int offset){


    }

}
class DataLocation{
    public DownloadFile file;
    public long offsetInFile;
    public int length;
    public int offsetInPiece;

    public DataLocation(DownloadFile file,long offsetInFile,int length,int offsetInPiece){
        this.file=file;
        this.offsetInFile=offsetInFile;
        this.offsetInPiece=offsetInPiece;
        this.length=length;
    }
}
enum PieceStatus{
    HAVE,UNFINISHED
}
