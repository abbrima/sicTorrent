import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class NetworkController {
    private static ArrayList<Torrent> torrents;
    private static ArrayList<Connection> connections;
    private static Server server;
    private static Thread serverThread;

    public static ArrayList<Connection> getConnections(){return connections;}

    public static void startServer() throws IOException {
        server = new Server();
        serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void killServer(){

        server.kill();

    }

    public static void addTorrent(Torrent t){torrents.add(t);}

    public static void invokeTorrents(){
        for (Torrent t:torrents){
            t.invokeThreads();
        }
    }

    public static ArrayList<Torrent> getTorrents(){return torrents;}

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
