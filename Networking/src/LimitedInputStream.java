import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.max;


public class LimitedInputStream extends DataInputStream {
    private BandwidthController controller;

    public LimitedInputStream(InputStream is) {
        super(is);
    }

    public void setController(BandwidthController ctrlr) {
        this.controller = ctrlr;
    }

    public byte[] readNBytesLimited(int length) throws IOException {
        byte inarr[] = new byte[length];
        if (!controller.downstreamLimited()) {
            readNBytes(inarr, 0, length);
        } else {
            int readBytes = 0;
            while (readBytes < length) {
                int bps = controller.requestDownBandwidth();
                long startTime = System.currentTimeMillis();
                if (bps < 0) {
                    readNBytes(inarr, readBytes, length - readBytes);
                    readBytes = length;
                } else if (bps >= 0 && bps < (length - readBytes)) {
                    readNBytes(inarr, readBytes, bps);
                    readBytes += bps;
                } else {
                    readNBytes(inarr, readBytes, length - readBytes);
                    readBytes = length;
                }
                long endTime = System.currentTimeMillis() - startTime;
                try {
                    Thread.sleep(max(1000 - endTime, 0));
                } catch (InterruptedException ie) {

                }
            }
        }
        return inarr;
    }

    public void requestBandwidth() {

        return;
    }

}

