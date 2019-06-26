import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.Math.max;

public class LimitedOutputStream extends DataOutputStream {
    private BandwidthController controller;

    public LimitedOutputStream(OutputStream os){
        super(os);
    }
    public void writeLimited(byte arr[]) throws IOException {
        if (!controller.upstreamLimited()) {
            write(arr);
        } else {
            int readBytes = 0;
            while (readBytes < arr.length) {
                int bps = controller.requestDownBandwidth();
                long startTime = System.currentTimeMillis();
                if (bps < 0) {
                    write(arr,readBytes,arr.length-readBytes);
                    readBytes = arr.length;
                } else if (bps >= 0 && bps < (arr.length - readBytes)) {
                    write(arr, readBytes, bps);
                    readBytes += bps;
                } else {
                    write(arr, readBytes, arr.length - readBytes);
                    readBytes = arr.length;
                }
                long endTime = System.currentTimeMillis() - startTime;
                try {
                    Thread.sleep(max(1000 - endTime, 0));
                } catch (InterruptedException ie) {

                }
            }
        }
    }
    public void setController(BandwidthController controller){
        this.controller=controller;
        this.controller=controller;
    }
}
