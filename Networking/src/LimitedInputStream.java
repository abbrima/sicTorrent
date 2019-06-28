import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static java.lang.Math.max;


public class LimitedInputStream extends DataInputStream {
    private BandwidthController controller;

    public LimitedInputStream(InputStream is) {
        super(is);
    }

    public byte[] readNBytes(int len) throws IOException {
        if (len < 0)
            throw new IndexOutOfBoundsException();
        byte arr[] = new byte[len];
        int n = 0;
        while (n < len) {
            int count = read(arr, n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return arr;
    }
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        Objects.requireNonNull(b);
        if (off < 0 || len < 0 || len > b.length - off)
            throw new IndexOutOfBoundsException();
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0)
                break;
            n += count;
        }
        return n;
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

