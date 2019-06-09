import java.io.Serializable;
import java.util.HashSet;

public class BandwidthController implements Serializable {
    private Torrent torrent;
    private int KBDown;
    private int KBUp;
    private String DownString;
    private String UpString;

    public String getDownString() {
        return DownString;
    }

    public String getUpString() {
        return UpString;
    }
    public boolean downstreamLimited(){
        return KBDown > 0;
    }
    public boolean upstreamLimited(){
        return KBUp < 0;
    }
//comment
    private transient HashSet<LimitedInputStream> istreamset;
    private transient HashSet<LimitedOutputStream> ostreamset;

    public BandwidthController(Torrent torrent) {
        this.torrent = torrent;
        istreamset = new HashSet<>();
        ostreamset = new HashSet<>();
        KBUp = -1;
        KBDown = -1;
    }

    public BandwidthController(Torrent torrent, int down, int up) {
        this(torrent);
        KBDown = down;
        KBUp = up;
    }

    public synchronized int requestDownBandwidth() {
        if (KBDown < 0)
            return KBDown;
        else
            return 1024 * KBDown / (istreamset.size()+1);
    }

    public synchronized int requestUpBandwidth() {
        if (KBUp < 0)
            return KBUp;
        else
            return KBUp / (ostreamset.size()+1);
    }
    public synchronized void startDownload(LimitedInputStream is){
        try{istreamset.add(is);}catch(Exception e){istreamset = new HashSet<>();
            ostreamset = new HashSet<>();}
    }
    public synchronized void startUpload(LimitedOutputStream os){
        try{ostreamset.add(os);}catch(Exception e){istreamset = new HashSet<>();
            ostreamset = new HashSet<>();}
    }

    public void freeDownBandwidth(LimitedInputStream is) {
        try {
            istreamset.remove(is);
        }catch(Exception e){istreamset = new HashSet<>();
            ostreamset = new HashSet<>();}
    }

    public void freeUpBandwidth(LimitedOutputStream os) {
        try {
            ostreamset.remove(os);
        }catch(Exception e){istreamset = new HashSet<>();
            ostreamset = new HashSet<>();}
    }
}
