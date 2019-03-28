
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Piece implements Serializable {
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

    public void dropRequest(){
        this.status=PieceStatus.UNFINISHED;
    }
    public void print() {
        for (DataLocation d : blockTable) {
            System.out.print("File: " + d.file.getPath() + " OffsetInFile: " + d.offsetInFile + " Length: " + d.length
                    + " OffsetInPiece: " + d.offsetInPiece);
            System.out.println("");
        }
        System.out.println("\n-----------------\n");
    }

    public Piece(int length, byte hash[], int index, Torrent torrent) {
        this.length = length;
        this.hash = hash;
        status = PieceStatus.UNFINISHED;
        blockTable = new ArrayList<>();
        this.index = index;
        this.torrent = torrent;
    }

    public byte[] getBlock(int offset, int size) throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readBytes = 0, fileOffset = 0;
        while (readBytes < size) {
            for (int i = 0; i < blockTable.size(); i++) {
                if (readBytes == size) break;
                DataLocation loc = blockTable.get(i);
                fileOffset = (offset - loc.offsetInPiece) + readBytes;
                if (i == blockTable.size() - 1) {
                    FileController.readBytesFromFile(loc.file,loc.offsetInFile+fileOffset,size-readBytes);
                    readBytes=size;
                } else {
                    if (offset >= blockTable.get(i + 1).offsetInPiece)
                        continue;
                    else {
                        int readable = Math.min(blockTable.get(i + 1).offsetInPiece - offset - readBytes, size - readBytes);
                        baos.writeBytes(FileController.readBytesFromFile(loc.file, loc.offsetInFile + fileOffset, readable));
                        readBytes+=readable;
                    }
                }
            }
        }
        return baos.toByteArray();
    }

    public void applyBytes(byte[] bytes, int offset) throws FileNotFoundException,IOException {
        int appliedBytes = 0, fileOffset = 0;
        while (appliedBytes < bytes.length) {
            for (int i = 0; i < blockTable.size(); i++) {
                if (appliedBytes == bytes.length) break;
                DataLocation loc = blockTable.get(i);
                fileOffset = (offset - loc.offsetInPiece) + appliedBytes;
                if (i == blockTable.size() - 1) {
                    FileController.writeBytesToFile(Arrays.copyOfRange(bytes, appliedBytes, bytes.length), loc.file, loc.offsetInFile + fileOffset);
                    appliedBytes = bytes.length;
                } else {
                    if (offset >= blockTable.get(i + 1).offsetInPiece) {
                        continue;
                    } else {
                        int writable = Math.min(blockTable.get(i + 1).offsetInPiece - offset - appliedBytes, bytes.length - appliedBytes);
                        FileController.writeBytesToFile(Arrays.copyOfRange(bytes, appliedBytes, appliedBytes + writable), loc.file, loc.offsetInFile + fileOffset);
                        appliedBytes += writable;
                    }
                }
            }
        }
        downloaded += bytes.length;
        torrent.addToDownloaded(bytes.length);
        if (downloaded == length)
            this.validate();
        else
            status=PieceStatus.UNFINISHED;
    }

    private void validate() throws FileNotFoundException,IOException {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-1");
            byte arr[] = hasher.digest(readPiece());
            if (Arrays.equals(arr, hash))
                status = PieceStatus.HAVE;
            else {
                status = PieceStatus.UNFINISHED;
                torrent.addToDownloaded(-length);
                downloaded = 0;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private byte[] readPiece() throws FileNotFoundException,IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < blockTable.size(); i++) {
            DataLocation loc = blockTable.get(i);
            baos.writeBytes(FileController.readBytesFromFile(loc.file, loc.offsetInFile, loc.length));
        }
        return baos.toByteArray();
    }

    public synchronized BlockRequest requestBlock() {
        if (Thread.interrupted()){return null;}
        int left = length - downloaded;
        this.status = PieceStatus.GETTING;
        if (left < Info.MaxBlockSize) {
            return new BlockRequest(left, downloaded, index);
        } else
            return new BlockRequest(Info.MaxBlockSize, downloaded, index);
    }

    public void addFileEntry(DownloadFile file, long offsetInFile, int length, int offsetInPiece) {
        blockTable.add(new DataLocation(file, offsetInFile, length, offsetInPiece));
    }
}

class DataLocation implements Serializable {
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

enum PieceStatus implements Serializable {
    HAVE, UNFINISHED, GETTING
}
