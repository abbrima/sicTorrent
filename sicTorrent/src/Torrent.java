
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Torrent implements Serializable {
    private byte infohash[];
    private long downloaded, uploaded;
    private long length;
    private int piecelength;
    private ArrayList<String> urllist;
    private transient ArrayList<Tracker> trackerlist;
    private int creationdate;
    private String comment;
    private String createdby;
    private String publisher;
    private String publisherurl;
    private ArrayList<Piece> pieces;
    private String name;
    private ArrayList<DownloadFile> files; //length path
    private transient TrackerManager trackermanager;
    private HashMap<String, Integer> peers;
    private TorrentStatus status;


    class TrackerManager implements Runnable {
        private ArrayList<String> trackerStrings;
        private Thread trackerThread;
        public void run(){

        }
        private void createTracker(String s) throws UnknownHostException {
                try {
                    if (!Tracker.checkIfExists(s, trackerlist))
                        trackerlist.add(Tracker.createTracker(s));
                } catch (InvalidTrackerException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {

                }
        }

        public void kill() {
        }

        public void addToTrackerList(ArrayList<String> list) {
            trackerStrings.addAll(list);
        }

        public void addToTrackerList(String s) {
            trackerStrings.add(s);
        }

        public void announceAll() {
            for (Tracker tracker : trackerlist) {
                if (tracker.isEnabled())
                    try {

                        for (Pair<String, Integer> pair : tracker.announce(infohash, uploaded, downloaded, length, AnnounceEvent.STARTED)) {
                            peers.put(pair.getFirst(), pair.getSecond());

                        }
                        System.out.println(tracker.getUri() + " Succeeded!");

                    } catch (Exception e) {
                        System.out.println(tracker.getUri() + " Failed!");
                    }
            }
        }

        public void scrapeAll() {
            for (Tracker tracker : trackerlist) {
                if (tracker.isEnabled()) {
                    try {
                        tracker.scrape(infohash).print();
                        System.out.println(tracker.getUri() + " Succeeded!");
                    } catch (Exception e) {
                        System.out.println(tracker.getUri() + " Failed!");
                    }
                }
            }
        }
    }

    public void test() {
        //trackerlist.get(2).disable();
        //System.out.println("ANNOUNCING: \n\n");
        //trackermanager.announceAll();
        //System.out.println("\n\nSCRAPING: \n\n");
        //trackermanager.scrapeAll();
        System.out.println("\n\n#seeds: " + peers.size());

        for (DownloadFile fl : files) {
            ArrayList<Piece> list = fl.getPieces();
            System.out.println(list.get(0).getIndex() + "   " + list.get(list.size() - 1).getIndex());
        }
    }

    public long getLeft() {
        return length - downloaded;
    }

    public void addToDownloaded(int l) {
        downloaded += l;
        if (downloaded == length)
            status = TorrentStatus.FINISHED;

    }

    public byte[] getInfoHash() {
        return infohash;
    }

    public HashMap<String, Integer> getPeers() {
        return peers;
    }


    public Torrent(Parcel parcel) {
        status = TorrentStatus.NEW;
        trackerlist = new ArrayList<>();
        trackermanager = new TrackerManager();
        infohash = parcel.getInfoHash();
        downloaded = 0;
        uploaded = 0;
        length = 0;
        for (int i = 0; i < parcel.getLength().size(); i++) length += parcel.getLength().get(i);
        piecelength = parcel.getPieceLength();
        urllist = parcel.getUrlList();
        trackermanager.addToTrackerList(parcel.getAnnounce());
        trackermanager.addToTrackerList(parcel.getAnnounceList());

        creationdate = parcel.getCreationDate();
        comment = parcel.getComment();
        createdby = parcel.getCreatedBy();
        publisher = parcel.getPublisher();
        publisherurl = parcel.getPublisherURL();
        pieces = new ArrayList<>();
        for (int i = 0; i < parcel.getHashValues().size(); i++) {
            pieces.add(new Piece(piecelength, parcel.getHashValues().get(i), i, this));
        }
        long t = pieces.size() * piecelength;
        pieces.get(pieces.size() - 1).setLength((int) (pieces.get(pieces.size() - 1).getLength() - (t - length)));
        name = parcel.getName();


        files = new ArrayList<>();

        if (parcel.getLength().size() == 1) {
            files.add(new DownloadFile(parcel.getLength().get(0), parcel.getPath().get(0)));
        } else {
            for (int i = 0; i < parcel.getLength().size(); i++)
                files.add(new DownloadFile(parcel.getLength().get(i), new String(name + "/" + parcel.getPath().get(i))));
        }

        peers = new HashMap<>();
        mapPiecesToFiles();
    }

    private void mapPiecesToFiles() {
        int fileIt = 0;
        DownloadFile currentFile = files.get(0);
        long offset = 0;
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            int mappedsize = 0;
            while (mappedsize < p.getLength()) {
                currentFile = files.get(fileIt);
                long fits = currentFile.getLength() - offset;
                if (fits > p.getLength() - mappedsize) {
                    p.addFileEntry(currentFile, offset, (int) (p.getLength() - mappedsize), mappedsize);
                    offset += p.getLength() - mappedsize;
                    mappedsize = p.getLength();
                } else {
                    p.addFileEntry(currentFile, offset, (int) fits, mappedsize);
                    fileIt++;
                    offset = 0;
                    mappedsize += fits;
                }
            }
        }
        offset = 0;
        int pieceIt = 0;
        for (fileIt = 0; fileIt < files.size(); fileIt++) {
            DownloadFile file = files.get(fileIt);
            ArrayList<Piece> list = new ArrayList<>();
            if (offset > 0) {
                list.add(pieces.get(pieceIt++));
            }
            while (offset < file.getLength()) {
                offset += pieces.get(pieceIt).getLength();
                list.add(pieces.get(pieceIt));
                if (offset <= file.getLength())
                    pieceIt++;
            }
            file.setPieces(list);
            offset -= file.getLength();
        }
    }

    public void doNotDownload(DownloadFile fl) {
        int index = files.indexOf(fl);
        int start, end;
        start = index - 1; //-1 for first file
        if (index == files.size() - 1)
            end = -1;
        else
            end = index + 1;
        if (start == -1)
            start = 0;
        else
            start = files.get(index - 1).getPieces().get(files.get(index - 1).getPieces().size() - 1).getIndex();
        if (end == -1)
            end = files.get(index).getPieces().get(files.get(index).getPieces().size() - 1).getIndex();
        else
            end = files.get(index).getPieces().get(0).getIndex();
        files.get(index).doNotDownload();
        for (int i = start; i <= end; i++) {
            pieces.get(i).doNotDownload();
        }
    }

    public void killThreads() {
        trackermanager.kill();
    }
}

enum TorrentStatus {
    FINISHED, STARTED, NEW
}