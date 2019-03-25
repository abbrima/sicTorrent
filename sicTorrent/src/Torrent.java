
import javafx.util.Pair;

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
    private ArrayList<byte[]> hashes;
    private String name;
    private FileMode filemode;
    private ArrayList<Pair<Long, String>> files; //length path
    private TrackerManager trackermanager;
    private HashMap<String, Integer> peers;

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
                            peers.put(pair.getKey(),pair.getValue());

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
        hashes = parcel.getHashValues();
        name = parcel.getName();
        files = new ArrayList<>();

        if (parcel.getLength().size() == 1) {
            filemode = FileMode.SINGLEFILE;
            files.add(new Pair<>(parcel.getLength().get(0), parcel.getPath().get(0)));
        } else {
            filemode = FileMode.MULTIFILE;
            for (int i = 0; i < parcel.getLength().size(); i++)
                files.add(new Pair<>(parcel.getLength().get(i), parcel.getPath().get(i)));
        }
        peers = new HashMap<>();

    }
}
