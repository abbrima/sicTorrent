import java.io.Serializable;
import java.util.ArrayList;

public class DownloadFile implements Serializable {
    private long length;
    private String path;
    private FileStatus status;
    private long downloaded;
    private String LengthString;
    private String DownloadedString;

    public String getLengthString(){return LengthString;}
    public String getDownloadedString(){return DownloadedString;}

    private ArrayList<Piece> pieces;

    public long getDownloaded() {
        return downloaded;
    }

    public synchronized void addToDownloaded(int num) {
        downloaded += num;
        DownloadedString = Funcs.lengthToStr(downloaded);
        if (length==downloaded)
            status = FileStatus.DOWNLOADED;
        else
            status = FileStatus.UNFINISHED;
            //validate();
    }
    public void download(){
        if (status==FileStatus.DONOTDOWNLOAD)
            status = FileStatus.UNFINISHED;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void doNotDownload() {
        if (status==FileStatus.UNFINISHED)
        status = FileStatus.DONOTDOWNLOAD;
    }
    public synchronized void validate() {
        boolean b = true;
        for (Piece p : pieces) {
            if (p.getStatus() != PieceStatus.HAVE) {
                b = false;
                break;
            }
        }
        if (b)
            status = FileStatus.DOWNLOADED;
    }

    public long getLength() {
        return length;
    }

    public String getPath() {
        return path;
    }

    public DownloadFile(long length, String path) {
        status = FileStatus.UNFINISHED;
        downloaded = 0;

        this.length = length;
        LengthString = Funcs.lengthToStr(this.length);
        DownloadedString = Funcs.lengthToStr(this.downloaded);
        this.path = path;
        try {
            FileController.createFile(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pieces = new ArrayList<>();
    }

}

enum FileStatus {
    DOWNLOADED, UNFINISHED, DONOTDOWNLOAD
}
