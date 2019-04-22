
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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
    private ConcurrentHashMap<String, Integer> peers;
    private TorrentStatus status;
    private transient ArrayList<Connection> connections;
    private Executor announceExecutor, scrapeExecutor;
    private transient PeerManager peermanager;

    class TrackerManager implements Runnable {
        private ArrayList<String> trackerStrings;
        public boolean kill;
        private int interval;
        private Thread trackerThread;

        public void run() {
            for (String s : trackerStrings)
                try {
                    boolean exists = false;
                    for (Tracker t : trackerlist)
                        if (t.getUri().toLowerCase().equals(s.toLowerCase()))
                            exists = true;
                    if (!exists)
                        createTracker(s);
                } catch (Exception e) {
                    return;
                }
            announceExecutor = Executors.newFixedThreadPool(trackerlist.size());
            scrapeExecutor = Executors.newFixedThreadPool(trackerlist.size());
            for (Tracker tracker : trackerlist) {
                scrape(tracker);
            }
            for (Tracker tracker : trackerlist)
                announce(tracker, AnnounceEvent.STARTED);
            while (kill == false) {
                interval = 100000;
                for (int i = 0; i < trackerlist.size(); i++) {
                    if (trackerlist.get(i).getInterval() > -1)
                        interval = Math.min(interval, trackerlist.get(i).getInterval());
                }
                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException ie) {
                    if (kill)
                        for (Tracker tracker : trackerlist) {
                            announce(tracker, AnnounceEvent.STOPPED);
                        }
                    return;
                }
                for (Tracker tracker : trackerlist) {
                    if (tracker.interval != -1)
                        tracker.interval -= interval;
                    if (tracker.interval <= 0 && tracker.status != TrackerStatus.TIMEDOUT)
                        announce(tracker, AnnounceEvent.NONE);
                }
            }
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

        public void forceAnnounce() {
            trackerThread.interrupt();
            trackerThread = new Thread(this);
            trackerThread.start();
        }

        public void announceFinished() {
            kill();
            for (Tracker tracker : trackerlist)
                announce(tracker, AnnounceEvent.COMPLETED);
        }

        public void kill() {
            kill = true;
            trackerThread.interrupt();
        }

        public void addToTrackerList(ArrayList<String> list) {
            trackerStrings.addAll(list);
        }

        public void addToTrackerList(String s) {
            trackerStrings.add(s);
        }

        public TrackerManager() {
            trackerStrings = new ArrayList<>();
            kill = false;
        }

        public void start() {
            interval = 0;
            trackerThread = new Thread(this);
            trackerThread.start();
        }

        public void announce(Tracker tracker, AnnounceEvent event) {
            announceExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        if (tracker.isEnabled() && tracker.status != TrackerStatus.TIMEDOUT) {
                            for (Pair<String, Integer> pair : tracker.announce(infohash, uploaded, downloaded, length, event))
                                peers.put(pair.getFirst(), pair.getSecond());
                        }
                    } catch (TimeoutException toe) {
                        tracker.status = TrackerStatus.TIMEDOUT;
                    } catch (IOException ioe) {
                        tracker.interval += 60;
                    } catch (InterruptedException ie) {
                    } catch (InvalidReplyException ire) {
                        tracker.interval += 30;
                    }
                }
            });
        }

        public void scrape(Tracker tracker) {
            scrapeExecutor.execute(new Runnable() {
                public void run() {
                    ScrapeResult result;
                    if (tracker.isEnabled() && tracker.status != TrackerStatus.TIMEDOUT)
                        try {
                            result = tracker.scrape(infohash);
                            tracker.seeds = result.getSeeders();
                            tracker.leeches = result.getLeechers();
                            tracker.downloaded = result.getDownloaded();
                        } catch (TimeoutException toe) {
                            tracker.status = TrackerStatus.TIMEDOUT;
                        } catch (InterruptedException ie) {
                        } catch (IOException ioe) {
                            tracker.interval += 60;
                        } catch (InvalidReplyException ire) {
                            tracker.interval += 30;
                        }
                }
            });
        }

    }

    class PeerManager implements Runnable {
        private Thread peerThread;
        private int connectionLimit;
        private boolean kill;

        public void run() {
            while (peers.size() == 0)
                Thread.yield();
            while (true) {
                if (peers.size() == 0) {
                    trackermanager.forceAnnounce();
                    while (peers.size()==0)
                        Thread.yield();
                }
                ExecutorService ex = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                Map<String,Integer> map = new ConcurrentHashMap<String,Integer>(peers);
                map.forEach((ip, port) -> {
                    try {
                        if (connections.size()<connectionLimit)
                        {Connection c = new Connection(Torrent.this, ip, port);
                        connections.add(c);
                        ex.execute(c.sending());
                        if(!c.hasFailed()){
                            ex.execute(c.receiving());
                        }
                       }
                    } catch (Exception e) {
                        System.out.println("CAN'T CONNECT");
                        peers.remove(ip);
                    }
                });
                for (int i=0;i<connections.size();i++)
                    if (connections.get(i).dead())
                        connections.remove(i);
            }
        }

        public PeerManager(int limit) {
            this.connectionLimit = limit;
            kill = false;
        }

        public void start() {
            peerThread = new Thread(this);
            peerThread.start();
        }

        public void kill() {
            kill = true;
            peerThread.interrupt();
        }
    }

    public void test() {

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

    public ConcurrentHashMap<String, Integer> getPeers() {
        return peers;
    }

    public Torrent() {
        peermanager = new PeerManager(10);
        trackermanager = new TrackerManager();
        connections = new ArrayList<>();
    }

    public Torrent(Parcel parcel) {
        status = TorrentStatus.NEW;
        connections = new ArrayList<>();

        trackerlist = new ArrayList<>();
        trackermanager = new TrackerManager();
        peermanager = new PeerManager(10);
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

        peers = new ConcurrentHashMap<>();
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

    public ArrayList<DownloadFile> getFiles() {
        return this.files;
    }

    public void killThreads() {
        trackermanager.kill();
    }
    public ArrayList<Tracker> getTrackers(){
        return trackerlist;
}
    public void invokeThreads() {
        trackermanager.start();
        peermanager.start();
    }
    public ArrayList<Piece> getPieces(){return pieces;}

    private void getPeersNow() {
        trackermanager.forceAnnounce();
    }
    enum TorrentStatus {
        FINISHED, STARTED, NEW
    }
}
