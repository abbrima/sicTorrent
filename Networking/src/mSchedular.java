import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.*;

public class mSchedular implements Runnable{
   private Thread thrd;

   public mSchedular(){}

   public void start(){
       thrd = new Thread(this);
       thrd.setDaemon(true);
       thrd.start();
   }
   public void forceStart(){
       thrd.interrupt();
       try{
           Thread.sleep(500);
       }catch(Exception e){}
       start();
   }
   private boolean between(){
       LocalTime current = LocalTime.now();
       if (mParameters.start.compareTo(mParameters.finish) < 0)
           return current.isAfter(mParameters.start) && current.isBefore(mParameters.finish);
       else
           return current.isAfter(mParameters.start) || current.isBefore(mParameters.finish);
   }
   private long difference(LocalTime from, LocalTime to){
       int a = from.getHour(); int b = to.getHour();
       if (b<a)
           b+=24;
       return (b-a)*3600*1000;
   }
   public void run(){
       while (true){
           if (between())
           {
               if (mParameters.scheduleEnabled)
                  NetworkController.invokeTorrents();
               try{
                   Thread.sleep(difference(LocalTime.now(),mParameters.finish) + 2000);
               }catch(InterruptedException ie){
                   return;
               }
           }
           else
           {
               if (mParameters.scheduleEnabled)
                   NetworkController.killTorrents();
               try{
                   Thread.sleep(difference(LocalTime.now(),mParameters.start));
               }catch(InterruptedException ie){
                   return;
               }
           }
       }
   }
}