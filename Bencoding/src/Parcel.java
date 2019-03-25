import java.util.ArrayList;

public abstract class Parcel {
    public Parcel() {
    }

    public void setUTF8Hash(String s) {
    }

    public String getUTF8Hash() {
        return null;
    }

    public void setDownloaded(int s) {
    }
    public int getDownloaded(){return 0;}

    public void setInfoHash(byte arr[]) {
    }

    public byte[] getInfoHash() {
        return null;
    }

    public void setMinRequestInterval(int s) {
    }

    public int getMinRequestInterval() {
        return 0;
    }

    public void setFailureReason(String s) {
    }

    public void setWarningMessage(String s) {
    }

    public void setInterval(int s) {
    }

    public void setMinInterval(int s) {
    }

    public void setTrackerID(String s) {
    }

    public void setComplete(int s) {
    }

    public void setIncomplete(int s) {
    }

    public void addToPeerID(String s) {
    }

    public void addToPeerPort(int s) {
    }

    public void addToPeerIP(String s) {
    }

    public void addToPeers(byte arr[]) {
    }

    public String getFailureReason() {
        return null;
    }

    public String getWarningMessage() {
        return null;
    }

    public int getInterval() {
        return 0;
    }

    public int getMinInterval() {
        return 0;
    }

    public String getTrackerID() {
        return null;
    }

    public int getComplete() {
        return 0;
    }

    public int getIncomplete() {
        return 0;
    }

    public ArrayList<String> getPeerID() {
        return null;
    }

    public ArrayList<String> getPeerIP() {
        return null;
    }

    public ArrayList<Integer> getPeerPort() {
        return null;
    }

    public ArrayList<byte[]> getPeers() {
        return null;
    }


    public abstract boolean validate();

    public String getPublisherURL() {
        return null;
    }

    public String getAnnounce() {
        return null;
    }

    public ArrayList<String> getUrlList() {
        return null;
    }

    public void addToUrlList(String s) {
    }

    public ArrayList<String> getAnnounceList() {
        return null;
    }

    public int getCreationDate() {
        return 0;
    }

    public String getComment() {
        return null;
    }

    public String getCreatedBy() {
        return null;
    }

    public String getEncoding() {
        return null;
    }

    public int getPieceLength() {
        return 0;
    }

    public ArrayList<byte[]> getHashValues() {
        return null;
    }

    public int getPrivate() {
        return 0;
    }


    public String getName() {
        return null;
    }

    public ArrayList<Long> getLength() {
        return null;
    }

    public ArrayList<String> getMd5sum() {
        return null;
    }

    public ArrayList<String> getPath() {
        return null;
    }

    public String getPublisher() {
        return null;
    }

    public abstract void print();

    public void setAnnounce(String s) {
    }

    public void addToAnnounceList(String s) {
    }

    public void setCreationDate(int s) {
    }

    public void setPublisherURL(String s) {
    }

    public void setComment(String s) {
    }

    public void setCreatedBy(String s) {
    }

    public void setEncoding(String s) {
    }

    public void setPieceLength(int s) {
    }

    public void setHashValues(ArrayList<byte[]> s) {
    }

    public void setPrivate(int s) {
    }


    public void setName(String s) {
    }

    public void addToLength(long s) {
    }

    public void addToMd5sum(String s) {
    }

    public void addToPath(String s) {
    }

    public void setPublisher(String s) {
    }
}

class TorrentParcel extends Parcel {
    public TorrentParcel() {
        announceList = new ArrayList<>();
        length = new ArrayList<>();
        path = new ArrayList<>();
        md5sum = new ArrayList<>();
        urllist = new ArrayList<>();
    }

    private ArrayList<String> urllist;
    private String announce;
    private ArrayList<String> announceList;
    private int creationDate;
    private String comment;
    private String createdBy;
    private String encoding;
    private String publisher;
    private String publisherURL;

    public ArrayList<String> getUrlList() {
        return urllist;
    }

    public void addToUrlList(String s) {
        urllist.add(s);
    }

    public void print() {
        System.out.println("Announce:  " + announce);
        for (String s : announceList)
            System.out.println("AnnounceList:  " + s);
        System.out.println("Creation Date:  " + creationDate);
        System.out.println("Comment:  " + comment);
        System.out.println("Created By:  " + createdBy);
        System.out.println("ENCODING:  " + encoding);
        System.out.println("Piece Length:  " + pieceLength + " Bytes");
        System.out.println("PRIVATE: " + _private);
        System.out.println("Name:  " + name);
        if (path.size() == 0) {
            System.out.println("Length in bytes:  " + length.get(0));
            try {
                System.out.println("md5sum  : " + md5sum.get(0));
            } catch (Exception e) {
            }
        } else
            for (int i = 0; i < path.size(); i++) {
                System.out.println("");
                System.out.println("Path:  " + path.get(i));
                System.out.println("Length:  " + length.get(i));
                try {
                    System.out.println("md5sum:  " + md5sum.get(i));
                } catch (Exception e) {
                }
            }
        System.out.println("Publisher:   " + publisher);
        System.out.println("Publisher URL:  " + publisherURL);

        for (byte arr[] : hashValues) {
            System.out.println("");
            for (byte b : arr)
                System.out.print((char) b);
            System.out.println(arr.length);
        }
    }

    private int pieceLength;
    private ArrayList<byte[]> hashValues;
    private int _private;
    private String name;
    private ArrayList<Long> length;
    private ArrayList<String> md5sum;
    private ArrayList<String> path;
    private byte[] infohash;

    public String getAnnounce() {
        return this.announce;
    }

    public String getPublisherURL() {
        return this.publisherURL;
    }

    public ArrayList<String> getAnnounceList() {
        return this.announceList;
    }

    public int getCreationDate() {
        return this.creationDate;
    }

    public void setInfoHash(byte[] arr) {
        this.infohash = arr;
    }

    public byte[] getInfoHash() {
        return infohash;
    }

    public String getComment() {
        return this.comment;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public int getPieceLength() {
        return this.pieceLength;
    }

    public ArrayList<byte[]> getHashValues() {
        return this.hashValues;
    }

    public int getPrivate() {
        return this._private;
    }


    public String getName() {
        return this.name;
    }

    public ArrayList<Long> getLength() {
        return this.length;
    }

    public ArrayList<String> getMd5sum() {
        return this.md5sum;
    }

    public ArrayList<String> getPath() {
        return this.path;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setAnnounce(String s) {
        this.announce = s;
    }

    public void addToAnnounceList(String s) {
        this.announceList.add(s);
    }

    public void setCreationDate(int s) {
        this.creationDate = s;
    }

    public void setComment(String s) {
        this.comment = s;
    }

    public void setPublisherURL(String s) {
        this.publisherURL = s;
    }

    public void setCreatedBy(String s) {
        this.createdBy = s;
    }

    public void setEncoding(String s) {
        this.encoding = s;
    }

    public void setPieceLength(int s) {
        this.pieceLength = s;
    }

    public void setHashValues(ArrayList<byte[]> s) {
        this.hashValues = s;
    }

    public void setPrivate(int s) {
        this._private = s;
    }


    public void setName(String s) {
        this.name = s;
    }

    public void addToLength(long s) {
        this.length.add(s);
    }

    public void addTotMd5sum(String s) {
        this.md5sum.add(s);
    }

    public void addToPath(String s) {
        this.path.add(s);
    }

    public void setPublisher(String s) {
        this.publisher = s;
    }

    public boolean validate() {
        if (announce == null)
            return false;
        if (pieceLength == 0)
            return false;
        if (hashValues.size() == 0)
            return false;
        if (_private != 1 && _private != 0)
            return false;
        if (name == null)
            return false;
        if (length.size() == 0)
            return false;
        else if (length.size() == 1) {
            if (md5sum.size() != 0)
                if (md5sum.size() == 1)
                    if (md5sum.get(0).length() == 32)
                        try {
                            Long.parseLong(md5sum.get(0), 16);
                        } catch (Exception ex) {
                            return false;
                        }
                    else
                        return false;
                else
                    return false;
        } else {
            int size = length.size();
            if (path.size() != size)
                return false;
            if (md5sum.size() != 0)
                if (md5sum.size() != size)
                    return false;
                else
                    for (String s : md5sum) {
                        if (s.length() == 32)
                            try {
                                Long.parseLong(md5sum.get(0), 16);
                            } catch (Exception ex) {
                                return false;
                            }
                        else
                            return false;

                    }
        }

        return true;
    }
}

class ScrapeParcel extends Parcel {
    public boolean validate() {
        return true;
    }

    public void print() {
    }

    private int complete;
    private int incomplete;
    private int downloaded;
    private String name;
    private String failurereason;
    private int minrequestinterval;
    private String utf8hash;

    public void setComplete(int s) {
        this.complete = s;
    }

    public void setUTF8Hash(String s) {
        this.utf8hash = s;
    }

    public void setIncomplete(int s) {
        this.incomplete = s;
    }

    public void setDownloaded(int s) {
        this.downloaded = s;
    }

    public void setName(String s) {
        this.name = s;
    }

    public void setFailureReason(String s) {
        this.failurereason = s;
    }

    public void setMinRequestInterval(int s) {
        this.minrequestinterval = s;
    }

    public int getDownloaded() {
        return downloaded;
    }

    public int getComplete() {
        return complete;
    }

    public int getIncomplete() {
        return incomplete;
    }

    public String getName() {
        return name;
    }

    public String getUTF8Hash() {
        return utf8hash;
    }

    public String getFailureReason() {
        return failurereason;
    }

    public int getMinRequestInterval() {
        return minrequestinterval;
    }

}

class ResponseParcel extends Parcel {
    ResponseParcel() {
        peerID = new ArrayList<>();
        peers = new ArrayList<>();
        peerIP = new ArrayList<>();
        peerPort = new ArrayList<>();
    }

    private String failureReason;
    private String warningMessage;
    private int interval;
    private int minInterval;
    private String trackerID;
    private int complete;
    private int incomplete;
    private ArrayList<String> peerID, peerIP;
    private ArrayList<Integer> peerPort;
    private ArrayList<byte[]> peers;

    public void setFailureReason(String s) {
        this.failureReason = s;
    }

    public void setWarningMessage(String s) {
        this.warningMessage = s;
    }

    public void setInterval(int s) {
        this.interval = s;
    }

    public void setMinInterval(int s) {
        this.minInterval = s;
    }

    public void setTrackerID(String s) {
        this.trackerID = s;
    }

    public void setComplete(int s) {
        this.complete = s;
    }

    public void setIncomplete(int s) {
        this.incomplete = s;
    }

    public void addToPeerID(String s) {
        this.peerID.add(s);
    }

    public void addToPeerPort(int s) {
        this.peerPort.add(s);
    }

    public void addToPeerIP(String s) {
        this.peerIP.add(s);
    }

    public void addToPeers(byte arr[]) {
        this.peers.add(arr);
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public int getInterval() {
        return interval;
    }

    public int getMinInterval() {
        return minInterval;
    }

    public String getTrackerID() {
        return trackerID;
    }

    public int getComplete() {
        return complete;
    }

    public int getIncomplete() {
        return incomplete;
    }

    public ArrayList<String> getPeerID() {
        return peerID;
    }

    public ArrayList<String> getPeerIP() {
        return peerIP;
    }

    public ArrayList<Integer> getPeerPort() {
        return peerPort;
    }

    public ArrayList<byte[]> getPeers() {
        return peers;
    }

    public void print() {
        System.out.println("Failure Reason:  " + failureReason);
        System.out.println("Warning Message:  " + warningMessage);
        System.out.println("Interval:  " + interval);
        System.out.println("MinInterval:  " + minInterval);
        System.out.println("TrackerID:  " + trackerID);
        System.out.println("Seeds:  " + complete);
        System.out.println("Leeches:  " + incomplete);
        for (int i = 0; i < peerIP.size(); i++) {
            try {
                System.out.println(peerID.get(i));
            } catch (Exception e) {
            }
            try {
                System.out.println(peerIP.get(i));
            } catch (Exception e) {
            }
            try {
                System.out.println(peerPort.get(i));
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < peers.size(); i++) {

            byte peer[] = peers.get(i);
            for (int j = 0; j < 4; j++) {
                System.out.print(Byte.toUnsignedInt(peer[j]));
                if (j != 3)
                    System.out.print(".");

            }
            System.out.println(":" + ((Byte.toUnsignedInt(peer[4]) * 256) + Byte.toUnsignedInt(peer[5])));
        }
    }

    public boolean validate() {

        return true;


    }
}


enum ParcelType {
    TORRENT, RESPONSE, SCRAPE
}