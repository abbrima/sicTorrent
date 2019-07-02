import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetworkController {
    private static ArrayList<Torrent> torrents;
    private static List<Connection> connections;
    private static Server server;

    public static List<Connection> getConnections() {
        return connections;
    }

    public static void startServer() throws IOException {
        server = new Server();
    }

    public static void killServer() {

        if (server!=null)
           server.kill();

    }
    public static void killTorrents(){
        for (Torrent t:torrents){
            t.killThreads();
        }
    }
    public static void addTorrent(Torrent t) {
        torrents.add(t);
    }
    public static void addTorrents(ArrayList<Torrent> ts){
        torrents.addAll(ts);
    }

    public static void invokeTorrents() {
        for (Torrent t : torrents) {
            t.invokeThreads();
        }
    }

    public static ArrayList<Torrent> getTorrents() {
        return torrents;
    }

    public static Torrent checkIfTorrentExists(byte arr[]) {
        for (Torrent t : torrents)
            if (Arrays.equals(t.getInfoHash(), arr))
                return t;
        return null;
    }

    public static boolean ipExists(String ip) {
        synchronized(connections){
           for (Connection c:connections){
               if (c.getIP().equals(ip))
                   return true;
           }
        }
        return false;
    }

    static {
        torrents = new ArrayList<>();
        connections = Collections.synchronizedList(new ArrayList<>());
    }
}
