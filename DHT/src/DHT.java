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
    public static void main(String args[]){

    }
}
