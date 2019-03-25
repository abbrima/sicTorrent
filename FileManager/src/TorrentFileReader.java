import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class TorrentFileReader {
    public static byte[] readFile(String path) throws IOException
    {
        File fl = new File(path);
        ArrayList<Byte> list = new ArrayList<>();
        byte arr[];

        arr = Files.readAllBytes(fl.toPath());

        for (int i=0;i<list.size();i++)
            arr[i]=list.get(i);

        return arr;
    }
}

