import java.io.Serializable;
import java.util.ArrayList;

public class DownloadFile implements Serializable {
    private long length;
    private String path;

    private ArrayList<Piece> pieces;
    public void setPieces(ArrayList<Piece> pieces){
        this.pieces=pieces;
    }
    public ArrayList<Piece> getPieces(){return pieces;}


    public long getLength(){return length;}
    public String getPath(){return path;}

    public DownloadFile(long length,String path){
        this.length=length;
        this.path=path;
        try{FileController.createFile(this);} catch(Exception e){e.printStackTrace();}
        pieces=new ArrayList<>();
    }

}
enum FileStatus{
    DOWNLOADED,UNFINISHED,DONOTDOWNLOAD
}
