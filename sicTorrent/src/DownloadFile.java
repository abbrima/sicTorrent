public class DownloadFile {
    private long length;
    private String path;

    public long getLength(){return length;}
    public String getPath(){return path;}

    public DownloadFile(long length,String path){
        this.length=length;
        this.path=path;
    }
}
