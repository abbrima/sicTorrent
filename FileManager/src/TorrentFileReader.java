import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class TorrentFileReader {
    public static byte[] readFile(String path) throws IOException
    {
        File fl = new File(path);
        byte arr[];

        arr = Files.readAllBytes(fl.toPath());

        return arr;
    }
}

