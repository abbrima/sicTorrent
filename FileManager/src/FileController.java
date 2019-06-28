import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileController {
    public synchronized static void writeBytesToFile(byte[] arr, DownloadFile file, long offset,String dir)
            throws IOException
    {
        File fl = new File(dir+file.getPath());
        if (!fl.exists()) throw new FileNotFoundException();
        RandomAccessFile raf = new RandomAccessFile(fl,"rw");
        raf.seek(offset);
        raf.write(arr);
        file.addToDownloaded(arr.length);
        file.validate();
    }

    public static byte[] readBytesFromFile(DownloadFile file, long offset, long length,String dir)
            throws FileNotFoundException, IOException
    {
        byte arr[]=new byte[(int)length]; File fl = new File(dir+file.getPath());
        if (!fl.exists()) throw new FileNotFoundException();
        RandomAccessFile raf = new RandomAccessFile(fl,"r");
        raf.seek(offset);
        raf.readFully(arr);
        return arr;
    }

    public static void createFile(DownloadFile file,String dir) throws IOException,FileNotFoundException
    {
        Pair<String, String> pair = splitPath(file.getPath());
        Files.createDirectories(Paths.get(dir+pair.getFirst()));
        try{Files.createFile(Paths.get(dir+pair.getFirst()+pair.getSecond()));
        File fl = new File(dir+file.getPath());
        if (!fl.exists()){throw new FileNotFoundException();}
        }catch(FileAlreadyExistsException e){}
    }

    private static Pair<String, String> splitPath(String path)
    {
        String dir = new String(), file = new String();
        String arr[] = path.split("/");
        if (arr.length > 1)
            for (int i = 0; i < arr.length - 1; i++)
                dir += arr[i] + "/";
        return new Pair<>(dir, arr[arr.length - 1]);
    }
}
