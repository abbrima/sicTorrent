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

    public BandwidthController(Torrent torrent) {
        this.torrent = torrent;
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
            return 1024 * KBDown / (torrent.getConnections().size()+1);
    }

    public synchronized int requestUpBandwidth() {
        if (KBUp < 0)
            return KBUp;
        else
            return KBUp / (torrent.getConnections().size()+1);
    }
}
