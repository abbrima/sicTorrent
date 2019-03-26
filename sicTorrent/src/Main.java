import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.Random;

public class Main {
    public static void main(String args[]) throws Exception {
        //byte arr[] = TorrentFileReader.readFile("files/fb.torrent");
       // init();
        //Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
       // torrent.test();
        Files.createDirectories(Paths.get("abcd/efg/hijk"));
    }

    private static void init() {
        Info.initPeerID();
        Info.initPort();
    }
}

//Remember to change request intervals from 15 to 1500
