
import java.util.ArrayList;
import java.util.Arrays;

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

    public void applyBytes(byte[] bytes, int offset) throws FileNotFoundException {
        int appliedBytes=0,fileOffset=0;
        while (appliedBytes<bytes.length){
            for (int i=0;i<blockTable.size();i++){
                DataLocation loc = blockTable.get(i);
                fileOffset=(offset-loc.offsetInPiece)+appliedBytes;
                if (i==blockTable.size()-1){
                    FileController.writeBytesToFile(Arrays.copyOfRange(bytes,appliedBytes,bytes.length),loc.file,loc.offsetInFile+fileOffset);
                    appliedBytes=bytes.length;
                }
                else{
                    if (offset>=blockTable.get(i+1).offsetInPiece){
                        continue;
                    }
                    else{
                        int writable=Math.max(blockTable.get(i+1).offsetInPiece-offset-appliedBytes,bytes.length-appliedBytes);
                        FileController.writeBytesToFile(Arrays.copyOfRange(bytes,appliedBytes,appliedBytes+writable),loc.file,loc.offsetInFile+fileOffset);
                        appliedBytes+=writable;
                    }
                }
            }
        }
        downloaded+=bytes.length;
        torrent.addToDownloaded(bytes.length);

        if (downloaded==length)
            this.validate();
    }
    private void validate(){

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
