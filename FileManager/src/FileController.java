import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileController {
    public static void writeBytesToFile(byte[] arr, DownloadFile file, long offset)
            throws FileNotFoundException, IOException {


    }

    public static byte[] readBytesFromFile(DownloadFile file, long offset, long lenth)
            throws FileNotFoundException, IOException {

        return null;
    }

    public static void createFile(DownloadFile file) throws IOException {
        Pair<String, String> pair = splitPath(file.getPath());
        Files.createDirectories(Paths.get(Parameters.downloadDir+pair.getFirst()));
        Files.createFile(Paths.get(Parameters.downloadDir+pair.getFirst()+pair.getSecond()));
    }

    private static Pair<String, String> splitPath(String path) {
        String dir = new String(), file = new String();
        String arr[] = path.split("/");
        if (arr.length > 1)
            for (int i = 0; i < arr.length - 1; i++)
                dir += arr[i] + "/";
        return new Pair<>(dir, arr[arr.length - 1]);
    }
}
