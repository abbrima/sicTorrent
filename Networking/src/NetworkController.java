import java.util.ArrayList;
import java.util.Arrays;

public class NetworkController {
    private static ArrayList<Torrent> torrents;
    private static ArrayList<Connection> connections;

    public static void addTorrent(Torrent t){torrents.add(t);}

    public static void invokeTorrents(){
        for (Torrent t:torrents){
            t.invokeThreads();
        }
    }

    public static boolean checkIfTorrentExists(byte arr[]) {
        for (Torrent t : torrents)
            if (Arrays.equals(t.getInfoHash(), arr))
                return true;

        return false;
    }

    static {
        torrents = new ArrayList<>();
        connections = new ArrayList<>();
    }
}
