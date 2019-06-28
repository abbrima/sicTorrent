
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Piece implements Serializable {
    private int index;
    private Integer length;
    private Integer downloaded;
    private byte hash[];
    private String DownloadedString;
    private String LengthString;
    private ArrayList<Block> blocks;

    private PieceStatus status;
    private Torrent torrent;

    public String getDownloadedString(){return DownloadedString;}
    public String getLengthString(){return LengthString;}

    public int getIndex() {
        return index;
    }

    public void doNotDownload() {
        for (Block b:blocks)
            b.cancelRequest();
        if (this.status==PieceStatus.UNFINISHED)
            this.status = PieceStatus.DONOTDOWNLOAD;
    }
    public void download() {if (status!=PieceStatus.HAVE)
    status = PieceStatus.UNFINISHED;}
    public PieceStatus getStatus() {
        return status;
    }

    public Integer getDownloaded() {
        return downloaded;
    }

    public int getLength() {
        return length;
    }

    private ArrayList<DataLocation> blockTable;

    public void setLength(int s) {
        this.length = s;
        LengthString = Funcs.lengthToStr(s);
    }

    public Piece(int length, byte hash[], int index, Torrent torrent) {
        this.length = length;
        this.hash = hash;
        downloaded = 0;
        status = PieceStatus.UNFINISHED;
        blockTable = new ArrayList<>();
        this.index = index;
        this.torrent = torrent;
        DownloadedString = Funcs.lengthToStr(downloaded);
        LengthString = Funcs.lengthToStr(this.length);
    }

    public void initBlocks() {
        blocks = new ArrayList<>();
        int off = 0;
        while (off + Info.MaxBlockSize < length) {
            blocks.add(new Block(Info.MaxBlockSize, off));
            off += Info.MaxBlockSize;
        }
        int rem = length - off;
        if (rem > 0)
            blocks.add(new Block(rem, off));
    }

    class Block implements Serializable {
        private int blength;
        private int boffset;
        private transient Thread t;
        private BlockStatus status;
        private transient ArrayList<Connection> connections;

        public Block(int length, int offset) {
            blength = length;
            boffset = offset;
            status = BlockStatus.READY;
        }

        public int getOffset() {
            return boffset;
        }

        public synchronized void setDownloaded(Connection c) {
            if (t!=null) t.interrupt();
            connections.remove(c);
            for (Connection con : connections)
                con.cancelRequest(new Triplet<>(index, boffset, blength));
            status = BlockStatus.DOWNLOADED;
        }

        public synchronized BlockStatus getStatus() {
            return status;
        }

        public synchronized Triplet<Integer, Integer, Integer> get(Connection c, boolean endgame) {
            if (connections == null)
                connections = new ArrayList<>();
            if (status == BlockStatus.READY && !endgame) {
                t = new Thread(() -> {
                    try {
                        Thread.sleep(15000);
                        cancelRequest();
                    } catch (InterruptedException e) {
                    }
                });
                t.setDaemon(true);
                t.start();
                status = BlockStatus.REQUESTED;
                connections.add(c);
                return new Triplet<>(index, boffset, blength);
            } else if (endgame && (status == BlockStatus.READY || status == BlockStatus.REQUESTED)) {
                status = BlockStatus.REQUESTED;
                connections.add(c);
                return new Triplet<>(index, boffset, blength);
            } else
                return null;
        }

        public void reset() {
            status = BlockStatus.READY;
        }

        public synchronized void cancelRequest() {
            if (connections != null)
                for (Connection c : connections)
                    c.cancelRequest(new Triplet<>(index, boffset, blength));
            if (status == BlockStatus.REQUESTED)
                status = BlockStatus.READY;
        }
    }

    enum BlockStatus {
        DOWNLOADED, READY, REQUESTED
    }


    public synchronized Triplet<Integer, Integer, Integer> requestBlock(Connection c, boolean endgame) {
        if (!endgame) {
            for (Block b : blocks) {
                if (b.getStatus() == BlockStatus.READY) {
                    Triplet<Integer, Integer, Integer> blk = b.get(c, endgame);
                    if (blk != null) return blk;
                }
            }
        } else {
            for (Block b : blocks) {
                if (b.getStatus() == BlockStatus.READY || b.getStatus() == BlockStatus.REQUESTED) {
                    Triplet<Integer, Integer, Integer> blk = b.get(c, endgame);
                    if (blk != null) return blk;
                }
            }
        }
        return null;
    }

    public void applyBytes(byte[] bytes, int offset, Connection c) throws FileNotFoundException, IOException {
        for (Block b : blocks)
            if (b.getOffset() == offset) {
                if (b.getStatus()==BlockStatus.DOWNLOADED)
                    return;
                b.setDownloaded(c);
                break;
            }
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
        synchronized(downloaded) {
            downloaded += bytes.length;
            DownloadedString = Funcs.lengthToStr(downloaded);
            torrent.addToDownloaded(bytes.length);
            if (downloaded.compareTo(length) == 0)
                this.validate();
            else if (downloaded.compareTo(length)>0)
                System.out.println("OVERFLOW");
            else
                status = PieceStatus.UNFINISHED;
        }
    }

    ///reset blocks here
    private void validate() throws FileNotFoundException, IOException {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-1");
            byte arr[] = hasher.digest(readPiece());
            if (Arrays.equals(arr, hash))
            {status = PieceStatus.HAVE; torrent.broadcastHave(this);}
            else {
                status = PieceStatus.UNFINISHED;
                torrent.addToDownloaded(-length);
                downloaded = 0;
                for (DataLocation loc : blockTable) {
                    loc.file.addToDownloaded(-loc.length);
                }
                for (Block b : blocks)
                    b.reset();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public synchronized void cancelGet() {
        for (Block b : blocks)
            b.cancelRequest();
    }

    public void addFileEntry(DownloadFile file, long offsetInFile, int length, int offsetInPiece) {
        blockTable.add(new DataLocation(file, offsetInFile, length, offsetInPiece));
    }

    private byte[] readPiece() throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < blockTable.size(); i++) {
            DataLocation loc = blockTable.get(i);
            baos.write(FileController.readBytesFromFile(loc.file, loc.offsetInFile, loc.length));
        }
        return baos.toByteArray();
    }

    public byte[] getBlock(int offset, int size) throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();



        int readBytes = 0, fileOffset;
            for (int i = 0; i < blockTable.size(); i++) {
                if (readBytes == size) break;
                DataLocation loc = blockTable.get(i);
                fileOffset = (offset - loc.offsetInPiece) + readBytes;
                if (i == blockTable.size() - 1) {
                    baos.write(FileController.readBytesFromFile(loc.file,
                            loc.offsetInFile + fileOffset, size - readBytes));
                    readBytes = size;
                } else {
                    if (offset >= blockTable.get(i + 1).offsetInPiece)
                        continue;
                    else {
                        int readable = Math.min(blockTable.get(i + 1).offsetInPiece - offset - readBytes, size - readBytes);
                        baos.write(FileController.readBytesFromFile(loc.file, loc.offsetInFile + fileOffset, readable));
                        readBytes += readable;
                    }
                }
            }
            if (readBytes!=size)
                throw new FileNotFoundException("HELLO");
        torrent.addToUploaded(baos.toByteArray().length);
        return baos.toByteArray();
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
    HAVE, UNFINISHED, DONOTDOWNLOAD
}