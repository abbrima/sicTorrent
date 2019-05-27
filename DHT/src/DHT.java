import java.net.URL;

public class DHT {
    public static int dist(byte a[],byte b[]){
       int dist = 0;
       for (int i=0;i<a.length;i++){
           int n = a[i]^b[i];
           for (int j=7;j>=0;j--)
           {
               if (n>=Math.pow(2,j))
               {
                   dist++;
                   n-=Math.pow(2,j);
               }
           }
       }
       return dist;
    }
    public static void main(String args[]) throws Exception{
        Tracker tracker = Tracker.createTracker("http://retracker.local/announce");
        System.out.println(tracker.getUri());
        String hexhash = "29B0A946E3FB759E442E085A81A5BFE115B73A4B";
        byte arr[] = Funcs.hexToByteArray(hexhash);
        try{
            tracker.announce(arr,0,0,0,AnnounceEvent.STARTED);
        }catch(Exception e){e.printStackTrace();}
    }
}
