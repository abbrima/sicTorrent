import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.*;

public class mSchedular implements Runnable{
    private Thread thrd;
    boolean alive = false;
    boolean stop = false;
    public void start(){
        if (alive)
            return;
        stop = false;
        thrd = new Thread(this);
        thrd.setDaemon(true);
        thrd.start();
    }
    private boolean between(){
        if (mParameters.start.compareTo(mParameters.finish) < 0)
            return LocalTime.now().compareTo(mParameters.start)>0 && LocalTime.now().compareTo(mParameters.finish)<0;
        else
            return LocalTime.now().compareTo(mParameters.start)>0 || LocalTime.now().compareTo(mParameters.finish)<0;
    }
    private long timeTillStart(){
        return Math.abs(LocalTime.now().until(mParameters.start, MILLIS));
    }
    private long timeTillStop(){
        return Math.abs(LocalTime.now().until(mParameters.finish, MILLIS));
    }
    public void run(){
        while(mParameters.scheduleEnabled && !stop){
         if (between())
         {
             if (stop)
                 break;
             ////sleep for difference////
             if (mParameters.scheduleEnabled)
             NetworkController.invokeTorrents();
             try{Thread.sleep(timeTillStop() + 2000);}catch(InterruptedException ie){
                 return;
             }
         }
         else{
             if (stop)
                 break;
             if (mParameters.scheduleEnabled)
             NetworkController.killTorrents();
             try{Thread.sleep(timeTillStart() + 2000);}catch(InterruptedException ie){return;}
             ////sleep for difference////
         }
        }
        alive = false;
    }
    public void forceStart(){
        stop = false;
        if (thrd!=null)
            thrd.interrupt();
        thrd = new Thread(this);
        thrd.setDaemon(true);
        thrd.start();
    }
}