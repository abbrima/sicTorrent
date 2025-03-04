import java.io.Serializable;
import java.util.HashSet;

public class BandwidthController implements Serializable {
    private Torrent torrent;
    private int KBDown;
    private int KBUp;
    public int getDown(){return KBDown;}
    public int getUp(){return KBUp;}

    public boolean downstreamLimited(){
        return KBDown > 0;
    }
    public boolean upstreamLimited(){
        return KBUp < 0;
    }
//comment

    public void setDownstream(int down){
        this.KBDown = down;
    }
    public void setUpstream(int up){
        this.KBUp = up;
    }
    public BandwidthController(Torrent torrent) {
        this.torrent = torrent;
        KBUp = -1;
        KBDown = -1;
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
