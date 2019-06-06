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
    private HashSet<LimitedInputStream> istreamset;
    private HashSet<LimitedOutputStream> ostreamset;

    public BandwidthController(Torrent torrent) {
        this.torrent = torrent;
        istreamset = new HashSet<>();
        ostreamset = new HashSet<>();
        KBUp = -1;
        KBDown = 20;
    }

    public BandwidthController(Torrent torrent, int down, int up) {
        this(torrent);
        KBDown = down;
        KBUp = up;
    }

    public synchronized int requestDownBandwidth(LimitedInputStream is) {
        istreamset.add(is);
        if (KBDown < 0)
            return KBDown;
        else
            return 1024 * KBDown / istreamset.size();
    }

    public synchronized int requestUpBandwidth(LimitedOutputStream os) {
        ostreamset.add(os);
        if (KBUp < 0)
            return KBUp;
        else
            return KBUp / ostreamset.size();
    }

    public void freeDownBandwidth(LimitedInputStream is) {
        istreamset.remove(is);
    }

    public void freeUpBandwidth(LimitedOutputStream os) {
        ostreamset.remove(os);
    }
}
