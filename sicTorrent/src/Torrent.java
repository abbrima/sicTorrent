
import java.util.ArrayList;
import java.util.HashMap;

public class Torrent {
    private byte infohash[];
    private long downloaded, uploaded;
    private long length;
    private int piecelength;
    private ArrayList<String> urllist;
    private ArrayList<Tracker> trackerlist;
    private int creationdate;
    private String comment;
    private String createdby;
    private String publisher;
    private String publisherurl;
    private ArrayList<Piece> pieces;
    private String name;
    private ArrayList<DownloadFile> files; //length path
    private TrackerManager trackermanager;
    private HashMap<String, Integer> peers;

    public long getLeft(){return length-downloaded;}
    public void addToDownloaded(int l){downloaded+=l;}
    public void test() {
        System.out.println("ANNOUNCING: \n\n");
        trackermanager.announceAll();
        System.out.println("\n\nSCRAPING: \n\n");
        trackermanager.scrapeAll();
        System.out.println("\n\n#seeds: "+peers.size());
    }

    class TrackerManager {
        public void announceAll() {
            for (Tracker tracker : trackerlist) {
                if (tracker.isEnabled())
                    try {

                        for (Pair<String, Integer> pair : tracker.announce(infohash, uploaded, downloaded, length, AnnounceEvent.STARTED)) {
                            peers.put(pair.getFirst(),pair.getSecond());

                        }
                        System.out.println(tracker.getUri() + " Succeeded!");

                    } catch (Exception e) {
                        System.out.println(tracker.getUri() + " Failed!");
                    }
            }
        }
        public void scrapeAll() {
            for (Tracker tracker: trackerlist) {
                if (tracker.isEnabled())
                {
                    try{
                        tracker.scrape(infohash).print();
                        System.out.println(tracker.getUri() + " Succeeded!");
                    }catch (Exception e){System.out.println(tracker.getUri() + " Failed!");}
                }
            }
        }
    }

    public Torrent(Parcel parcel) {
        trackerlist = new ArrayList<>();
        trackermanager = new TrackerManager();
        infohash = parcel.getInfoHash();
        downloaded = 0;
        uploaded = 0;
        length = 0;
        for (int i = 0; i < parcel.getLength().size(); i++) length += parcel.getLength().get(i);
        piecelength = parcel.getPieceLength();
        urllist = parcel.getUrlList();
        try {
            trackerlist.add(Tracker.createTracker(parcel.getAnnounce()));
        } catch (InvalidTrackerException e) {
        }
        for (String s : parcel.getAnnounceList())
            try {
                if (!Tracker.checkIfExists(s,trackerlist))
                trackerlist.add(Tracker.createTracker(s));
            } catch (InvalidTrackerException e) {
                e.printStackTrace();
            }
        creationdate = parcel.getCreationDate();
        comment = parcel.getComment();
        createdby = parcel.getCreatedBy();
        publisher = parcel.getPublisher();
        publisherurl = parcel.getPublisherURL();
        pieces=new ArrayList<>();
        for (int i=0;i<parcel.getHashValues().size();i++){
            pieces.add(new Piece(piecelength,parcel.getHashValues().get(i),i,this));
        }
        long t=pieces.size()*piecelength;
        pieces.get(pieces.size()-1).setLength((int)(pieces.get(pieces.size()-1).getLength()-(t-length)));
        name = parcel.getName();


        files = new ArrayList<>();

        if (parcel.getLength().size() == 1) {
            files.add(new DownloadFile(parcel.getLength().get(0), parcel.getPath().get(0)));
        } else {
            for (int i = 0; i < parcel.getLength().size(); i++)
                files.add(new DownloadFile(parcel.getLength().get(i),new String(name+"/"+parcel.getPath().get(i))));
        }

        peers = new HashMap<>();
        mapPiecesToFiles();
    }
    private void mapPiecesToFiles(){
        int fileIt=0; DownloadFile currentFile=files.get(0); long offset=0;
        for (int i=0;i<pieces.size();i++){
            Piece p=pieces.get(i); int mappedsize=0;
            while (mappedsize<p.getLength()){
                currentFile = files.get(fileIt);
                long fits=currentFile.getLength()-offset;
                if (fits>p.getLength()-mappedsize){
                    p.addFileEntry(currentFile,offset,(int)(p.getLength()-mappedsize),mappedsize);
                    offset+=p.getLength()-mappedsize;
                    mappedsize=p.getLength();
                }
                else{
                    p.addFileEntry(currentFile,offset,(int)fits,mappedsize);
                    fileIt++; offset=0; mappedsize+=fits;
                }
            }
        }
    }
    public HashMap<String, Integer> getPeers(){
        return peers;
    }
}
