
import java.util.ArrayList;

public class Piece {
    private int index;
    private int length;
    private int downloaded;
    private byte hash[];
    private PieceStatus status;
    private Torrent torrent;

    public PieceStatus getStatus() {
        return status;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getLength() {
        return length;
    }

    private ArrayList<DataLocation> blockTable;

    public void setLength(int s) {
        this.length = s;
    }

    public void print() {
        for (DataLocation d : blockTable) {
            System.out.print("File: " + d.file.getPath() + " OffsetInFile: " + d.offsetInFile + " Length: " + d.length
                    + " OffsetInPiece: " + d.offsetInPiece);
            System.out.println("");
        }
        System.out.println("\n-----------------\n");
    }

    public Piece(int length, byte hash[],int index,Torrent torrent) {
        this.length = length;
        this.hash = hash;
        status = PieceStatus.UNFINISHED;
        blockTable = new ArrayList<>();
        this.index=index;
        this.torrent=torrent;
    }

    public byte[] getBlock(int offset, int size) {

        return null;
    }

    public void applyBytes(byte[] bytes, int offset) {
        downloaded += bytes.length;
        torrent.addToDownloaded(bytes.length);


    }

    public BlockRequest requestBlock(){
        int left=length-downloaded;
        this.status=PieceStatus.GETTING;
        if (left<Info.MaxBlockSize){
            return new BlockRequest(left,downloaded,index);
        }
        else
            return new BlockRequest(Info.MaxBlockSize,downloaded,index);
    }

    public void addFileEntry(DownloadFile file, long offsetInFile, int length, int offsetInPiece) {
        blockTable.add(new DataLocation(file, offsetInFile, length, offsetInPiece));
    }
}

class DataLocation {
    public DownloadFile file;
    public long offsetInFile;
    public int length;
    public int offsetInPiece;

    public DataLocation(DownloadFile file, long offsetInFile, int length, int offsetInPiece) {
        this.file = file;
        this.offsetInFile = offsetInFile;
        this.offsetInPiece = offsetInPiece;
        this.length = length;
    }
}

enum PieceStatus {
    HAVE, UNFINISHED, GETTING
}
