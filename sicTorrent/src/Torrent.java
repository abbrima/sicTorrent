

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;

public class Torrent implements Serializable {
    private String downloadDir;
    private byte infohash[];
    private boolean linear;
    Long Downloaded,Uploaded;
    private long length;
    public void addConnection(Connection c) throws Exception{
        synchronized(connections){
            if (connections.size()<peermanager.getConnectionLimit() && status==TorrentStatus.ACTIVE)
                connections.add(c);
            else
                throw new Exception();
        }
    }
    public long getLength() {
        return length;
    }

    public boolean getLinear(){return linear;}
    public void setDownLimit(int down){
        bandwidthcontroller.setDownstream(down);
    }
    public void setUpLimit(int up){
        bandwidthcontroller.setUpstream(up);
    }

    public String getProgress() {
        return progress;
    }

    public Long getUploaded() {
        return Uploaded;
    }

    private String progress;
    private String DownloadedString;
    private String UploadedString;
    private String LengthString;

    public String getName() {
        return name;
    }
    public String getDownloadedString(){return DownloadedString;}
    public String getUploadedString(){return UploadedString;}
    public String getLengthString(){return LengthString;}

    public Long getDownloaded() {
        return Downloaded;
    }

    private int piecelength;
    private ArrayList<String> urllist;
    private ArrayList<String> trackerURLS;
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
    private transient Map<String, Integer> peers;
    private transient TorrentStatus status;
    private transient List<Connection> connections;
    private transient PeerManager peermanager;
    private transient boolean endgame = false;
    private BandwidthController bandwidthcontroller;

    class TrackerManager implements Runnable {
        private Thread trackerThread;
        private HashMap<String,TrackerHandler> handlers;
        public void run() {
            if (trackerlist == null)
                trackerlist = new ArrayList<>();
            for (String tString:trackerURLS){
                if (handlers.containsKey(tString)){
                    handlers.get(tString).start();
                }
                else{
                    TrackerHandler handler = new TrackerHandler(tString);
                    handler.start();
                    handlers.put(tString,handler);
                }
            }
        }
        public void forceAnnounce() {
            for (TrackerHandler handler:handlers.values()){
                handler.forceAnnounce();
            }
        }

        public void announceFinished() {
            for (TrackerHandler handler:handlers.values()){
                handler.announceFinished();
            }
        }

        public void kill() {
            trackerThread.interrupt();
            for (TrackerHandler h:handlers.values()){
                h.kill();
            }
        }


        public TrackerManager() {
            handlers = new HashMap<>();
        }

        public void start() {
            trackerThread = new Thread(this);
            trackerThread.start();
        }

        class TrackerHandler implements Runnable{
            String url; Thread thrd;
            private boolean dead=false;
            Tracker tracker;
            AnnounceEvent event;
            TrackerHandler(String url){
                this.url=url;
            }
            @Override public void run(){
                if (tracker==null){
                    while (true){
                        try {
                            tracker = Tracker.createTracker(url);
                            trackerlist.add(tracker);
                            new Thread(()->start()).start();
                            return;
                        }catch(InvalidTrackerException ite){
                            try{
                                System.out.println("CREATION FAILED");
                                Thread.sleep(30000);
                            }catch(InterruptedException ieee){return;}
                        }catch(UnknownHostException mr){
                            try{
                                System.out.println("CREATION FAILED");
                                Thread.sleep(30000);
                            }catch(InterruptedException ieee){return;}
                        }
                    }
                }
                else{
                    //manage tracker
                    event = AnnounceEvent.STARTED;
                    while (true){
                        if (dead)
                        {
                            tracker.setStatus(TrackerStatus.NONE);
                            return;
                        }
                        try{
                            ArrayList<Pair<String,Integer>> list = tracker.announce(infohash,Uploaded,Downloaded,length-Downloaded,event);
                            synchronized (peers){
                                for (Pair<String,Integer> pair:list)
                                    peers.put(pair.getFirst(),pair.getSecond());
                                peers.notifyAll();
                            }
                            switch(event){
                                case COMPLETED:
                                case NONE:
                                case STARTED:
                                    event = AnnounceEvent.NONE;
                                    break;
                                case STOPPED:
                                {
                                    tracker.setStatus(TrackerStatus.NONE);
                                    return;
                                }
                            }
                            try {
                                Thread.sleep(tracker.getInterval() * 1000);
                            }catch(IllegalArgumentException ieess){
                                Thread.sleep(12000);
                            }
                        }
                        catch(TimeoutException toe){
                            try{
                                tracker.setStatus(TrackerStatus.TIMEDOUT);
                                Thread.sleep(120000);
                            }catch(InterruptedException ie){

                            }
                        }catch(IOException ioe){
                            tracker.setStatus(TrackerStatus.UNKNOWN_HOST);
                            try {
                                Thread.sleep(12000);
                            }catch(InterruptedException ie){}
                        }catch(InterruptedException ie){

                        }catch(InvalidReplyException ire){
                            if (ire.getMessage().equals("Torrent not registered"))
                            {
                                tracker.setStatus(TrackerStatus.DISABLED);
                                return;
                            }
                            try{
                                if (!Thread.interrupted())
                                    Thread.sleep(120000);
                            }catch(InterruptedException ese){}
                        }
                    }
                }
            }
            public void kill(){
                synchronized(event) {
                    if (tracker.getStatus() != TrackerStatus.WORKING && tracker.getStatus() != TrackerStatus.UPDATING)
                        dead = true;
                    event = AnnounceEvent.STOPPED;
                }
                if (thrd!=null)
                    thrd.interrupt();
            }
            public void forceAnnounce(){
                if (thrd!=null)
                    thrd.interrupt();
            }
            public void announceFinished(){
                event = AnnounceEvent.COMPLETED;
                if (thrd!=null) thrd.interrupt();
            }
            public void start(){
                if (thrd!=null)
                    kill();
                dead = false;
                event = AnnounceEvent.STARTED;
                thrd = new Thread(this);
                thrd.start();
            }
        }

    }

    class PeerManager implements Runnable {
        private Thread peerThread;
        private int connectionLimit;
        private boolean kill;

        public void run() {
            if (!isFinished()) {
                if (peers.size() == 0)
                    try {
                        synchronized (peers) {
                            peers.wait();
                        }
                    } catch (InterruptedException ie) {
                        if (kill)
                            return;
                    }
                if (kill)
                    return;
            }
            while (!kill) {
                if (!isFinished()) {
                    if (peers.size() == 0) {
                        if (trackermanager != null)
                            trackermanager.forceAnnounce();
                        try {
                            synchronized (peers) {
                                peers.wait();
                            }
                        } catch (InterruptedException ie) {
                            if (kill)
                                return;
                        }
                        if (kill) return;
                    }
                    synchronized (peers) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.addAll(peers.keySet());
                        Collections.shuffle(list);
                        for (String ip : list) {
                            //try
                            {
                                if (connections.size() < connectionLimit && !NetworkController.ipExists(ip) && !kill) {
                                    Thread t = new Thread(() -> {
                                        Connection c = new Connection(Torrent.this, ip, peers.get(ip));

                                        if (status == TorrentStatus.ACTIVE) {
                                            synchronized (connections) {
                                                connections.add(c);
                                            }
                                            synchronized (NetworkController.getConnections()) {
                                                NetworkController.getConnections().add(c);
                                            }
                                        }
                                    });
                                    t.setDaemon(true);
                                    t.start();
                                    //create connection
                                } else if (connections.size() >= connectionLimit && Downloaded < length) {
                                    int closed = 0, i = 0;
                                    while (closed < 0 && i < connectionLimit)
                                        if (connections.get(i).getState() != ConnectionState.REQUEST) {
                                            connections.get(i++).closeSocket();
                                            closed++;
                                        }
                                } else
                                    break;
                            }
                        }
                    }
                }
                synchronized (connections) {
                    for (int i = 0; i < connections.size(); i++)
                        if (connections.get(i).failed()) {
                            NetworkController.getConnections().remove(connections.get(i));
                            connections.remove(i);
                        }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    if (kill) return;
                }
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
            if (peerThread != null)
                peerThread.interrupt();
        }

        public int getConnectionLimit(){return connectionLimit;}
    }

    public void test() {

    }

    public int getAvailiblePieces() {
        int count = 0;
        for (Piece p : pieces) {
            if (p.getStatus() == PieceStatus.HAVE)
                count++;
        }
        return count;
    }

    public synchronized Triplet<Integer, Integer, Integer> createRequest(boolean have[], Connection c) {
        synchronized (pieces) {
            if (!linear)
            {
                ArrayList<Piece> requested = new ArrayList<>(),notRequested = new ArrayList<>();
                for (Piece p : pieces){
                    if (p.getDownloaded()==0)
                        notRequested.add(p);
                    else
                        requested.add(p);
                }
                for (Piece p : requested) {
                    if (p.getStatus() == PieceStatus.UNFINISHED && have[p.getIndex()]) {
                        Triplet<Integer, Integer, Integer> blk = p.requestBlock(c, endgame);
                        if (blk != null)
                            return blk;
                    }
                }
                Collections.shuffle(notRequested);
                for (Piece p : notRequested) {
                    if (p.getStatus() == PieceStatus.UNFINISHED && have[p.getIndex()]) {
                        Triplet<Integer, Integer, Integer> blk = p.requestBlock(c, endgame);
                        if (blk != null)
                            return blk;
                    }
                }
            } else {
                for (Piece p : pieces) {
                    if (p.getStatus() == PieceStatus.UNFINISHED && have[p.getIndex()]) {
                        Triplet<Integer, Integer, Integer> blk = p.requestBlock(c, endgame);
                        if (blk != null)
                            return blk;
                    }
                }
            }
        }
        boolean state = true;
        for (DownloadFile fl:files)
            if (fl.getStatus()==FileStatus.DONOTDOWNLOAD)
                state = false;
        endgame = state;
        return null;
    }

    public long getLeft() {
        return length - Downloaded;
    }

    public void addToDownloaded(int l) {
        synchronized(Downloaded) {
            Downloaded += l;
            Formatter f = new Formatter();
            f.format("%6.2f%s",(double)Downloaded/length * 100,"%");
            progress = f.toString();
            DownloadedString = Funcs.lengthToStr(Downloaded);
            if (Downloaded == length) {
                trackermanager.announceFinished();
                Thread t = new Thread(() -> {
                    for (Connection c : connections) {
                        try{c.setInterested(false);}catch(Exception e){}
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        }
    }

    public void addToUploaded(int l) {
        synchronized (Uploaded){Uploaded+= l;
        UploadedString = Funcs.lengthToStr(Uploaded);
        }
    }

    public byte[] getInfoHash() {
        return infohash;
    }


    public Torrent() {
        peers = Collections.synchronizedMap(new HashMap<>());
        connections = Collections.synchronizedList(new ArrayList<Connection>());
    }
    public BandwidthController getBandwidthcontroller(){
        return bandwidthcontroller;
    }

    public Torrent(Parcel parcel) {
        this();
        downloadDir = Parameters.downloadDir;
        Downloaded = (long)0;        Uploaded = (long)0;
        bandwidthcontroller = new BandwidthController(this);
        status = TorrentStatus.INACTIVE;
        linear = true;
        progress = "0%";
        DownloadedString = Funcs.lengthToStr(Downloaded);
        UploadedString = Funcs.lengthToStr(Uploaded);
        status = TorrentStatus.INACTIVE;
        connections = new ArrayList<>();

        //trackermanager = new TrackerManager();
       // peermanager = new PeerManager(50);
        infohash = parcel.getInfoHash();
        length = 0;
        for (int i = 0; i < parcel.getLength().size(); i++) length += parcel.getLength().get(i);
        LengthString = Funcs.lengthToStr(length);
        piecelength = parcel.getPieceLength();
        urllist = parcel.getUrlList();

        trackerURLS = new ArrayList<>();
        trackerURLS.add(parcel.getAnnounce());
        for (String s:parcel.getAnnounceList())
        {
            if (!trackerURLS.contains(s))
                trackerURLS.add(s);
        }

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
            files.add(new DownloadFile(parcel.getLength().get(0), parcel.getName(),this));
        } else {
            for (int i = 0; i < parcel.getLength().size(); i++)
                files.add(new DownloadFile(parcel.getLength().get(i), name + "/" + parcel.getPath().get(i),this));
        }

        mapPiecesToFiles();
        for (Piece p : pieces)
            p.initBlocks();
    }
    public void deleteFiles() throws IOException {
       // for (DownloadFile fl : files)
         //   fl.deleteFile();
        FileController.deleteDirectory(files.get(0),downloadDir);

    }
    public String getDownloadDir(){return downloadDir;}
    private void mapPiecesToFiles() {
        int fileIt = 0;
        DownloadFile currentFile;
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
        for (int i = 0; i < files.size(); i++) {
            DownloadFile file = files.get(i);
            ArrayList<Piece> pcs = new ArrayList<>();
            if (offset > 0) {
                pcs.add(pieces.get(pieceIt));
            }
            while (offset < file.getLength()) {
                pcs.add(pieces.get(pieceIt));
                offset += pieces.get(pieceIt).getLength();
                if (offset < file.getLength())
                    pieceIt++;
            }
            file.setPieces(pcs);
            offset -= file.getLength();
        }
    }

    public void doNotDownload(DownloadFile fl) {
        if (fl.getStatus() == FileStatus.UNFINISHED) {
            fl.doNotDownload();
            Piece start = fl.getPieces().get(0);
            Piece finish = fl.getPieces().size()>1?fl.getPieces().get(fl.getPieces().size()-1):null;
            for (int i=1;i<fl.getPieces().size()-1;i++)
                fl.getPieces().get(i).doNotDownload();
            int findex = files.indexOf(fl);
            try{
                if (!files.get(findex-1).getPieces().contains(start))
                    start.doNotDownload();
            }catch (Exception e){start.doNotDownload();}
            try{
                if (!files.get(findex+1).getPieces().contains(finish))
                    finish.doNotDownload();
            }catch (Exception e){finish.doNotDownload();}
        }
    }

    public void downloadFile(DownloadFile fl) {
        endgame=false;
        synchronized(fl.getPieces()) {
            for (Piece p : fl.getPieces())
            {
                synchronized(p) {
                    p.download();
                }
            }
        }
        fl.download();
        Thread t = new Thread(() -> {
            synchronized (connections) {
                for (Connection c : connections) {
                    try {
                        c.request();
                    }catch(Exception e){e.printStackTrace();}
                }
            }

        });
        t.setDaemon(true);
        t.start();
    }

    public ArrayList<DownloadFile> getFiles() {
        return this.files;
    }

    public void killThreads() {
        if (status == TorrentStatus.INACTIVE)
            return;
        trackermanager.kill();
        peermanager.kill();

        synchronized (peers) {
            peers.notify();
        }
        status = TorrentStatus.INACTIVE;
        synchronized (connections) {
            for (Connection c : connections) {
                {
                    synchronized (c) {
                        try {
                            c.closeSocket();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        connections.clear();
        for (Piece p : pieces)
            p.cancelGet();
        for (Tracker t:trackerlist)
            t.setStatus(TrackerStatus.NONE);
        if (Downloaded == length)
            progress = "Finished";
        else
            progress = "Paused";

    }

    public ArrayList<Tracker> getTrackers() {
        return trackerlist;
    }

    public void invokeThreads() {
        if (status == TorrentStatus.ACTIVE)
            return;
        Formatter f = new Formatter();
        f.format("%6.2f%s",(double)Downloaded/length * 100,"%");
        progress = f.toString();
        peers = Collections.synchronizedMap(new HashMap<>());
        peermanager = new PeerManager(30);
        if (trackermanager==null) {trackermanager = new TrackerManager();}
       // if (connections==null)
        connections = Collections.synchronizedList(new ArrayList<>());
        trackermanager.start();
        peermanager.start();
        for (DownloadFile file : files)
            file.validate();
        for (Piece p : pieces)
            p.cancelGet();
        status = TorrentStatus.ACTIVE;
    }

    public void broadcastHave(Piece p) {
        synchronized (connections) {
            for (Connection c : connections)
                synchronized (c) {
                    c.sendHave(p.getIndex());
                }
        }
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    private void getPeersNow() {
        trackermanager.forceAnnounce();
    }

    public boolean isFinished() {
        return length == Downloaded;
    }

    public TorrentStatus getStatus() {
        return status;
    }

    public void setLinear(boolean val) {
        linear = val;
    }
}

enum TorrentStatus {
    ACTIVE, INACTIVE
}