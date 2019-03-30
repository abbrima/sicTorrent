import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String args[]) throws Exception {

        //NetworkController.startServer();

        byte arr[] = TorrentFileReader.readFile("files/fb.torrent");
        Torrent torrent = new Torrent(bCoder.decode(arr, ParcelType.TORRENT));
        NetworkController.addTorrent(torrent);
        NetworkController.invokeTorrents();
          /*
        Connection handshake = new Connection();

        for (Map.Entry<String, Integer> entry : peersList.entrySet()) {
            InetAddress n = InetAddress.getByName(entry.getKey());
            System.out.println(entry.getKey() + "," + entry.getValue());
            handshake.sendHandshake(entry.getKey(), entry.getValue(), torrent.getInfoHash());
        }
        */

    }

}

