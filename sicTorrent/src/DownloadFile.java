import java.io.Serializable;
import java.util.ArrayList;

public class DownloadFile implements Serializable {
    private long length;
    private String path;
    private FileStatus status;
    private long downloaded;

    private ArrayList<Piece> pieces;
    public long getDownloaded(){return downloaded;}
    public void addToDownloaded(int num){downloaded+=num;}
    public void setPieces(ArrayList<Piece> pieces){
        this.pieces=pieces;
    }
    public ArrayList<Piece> getPieces(){return pieces;}
    public void doNotDownload(){status=FileStatus.DONOTDOWNLOAD;}
    public void validate(){
        boolean b=true;
        for (Piece p:pieces){
            if (p.getStatus()!=PieceStatus.HAVE)
                b=false;
        }
        if (b)
            status=FileStatus.DOWNLOADED;
    }

    public long getLength(){return length;}
    public String getPath(){return path;}

    public DownloadFile(long length,String path){
        status=FileStatus.UNFINISHED;
        downloaded=0;
        this.length=length;
        this.path=path;
        try{FileController.createFile(this);} catch(Exception e){e.printStackTrace();}
        pieces=new ArrayList<>();
    }

}
enum FileStatus{
    DOWNLOADED,UNFINISHED,DONOTDOWNLOAD
}
