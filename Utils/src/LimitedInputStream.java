import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;



public class LimitedInputStream extends DataInputStream {
    public LimitedInputStream(InputStream is){
        super(is);
    }
    public byte[] readNBytesLimited(int length)throws IOException{
        byte inarr[] = new byte[length];
        for (int i=0;i<length;i++)
            inarr[i] = readByte();

        return inarr;
    }

}






 class LimitedBandwidthStream extends FilterInputStream {
    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    protected LimitedBandwidthStream(InputStream in) {
        super(in);
    }
    private int bandwidth = 0;

    /** bandwidth limit will be calculated form the start time **/
    private boolean isReading = false;

    /** number of bytes read **/
    private int count = 0;

    /** check bandwidth every n bytes **/
    private static int CHECK_INTERVAL = 100;

    /** start time **/
    long starttime = 0;

    /** used time **/
    long usedtime = 0;


    /**
     * initializes the LimitedBandWidth stream
     */
    public LimitedBandwidthStream (InputStream in, int bandwidth)
            throws IOException
    {
        super(in);

        if (bandwidth > 0) {
            this.bandwidth=bandwidth;
        } else {
            this.bandwidth=0;
        }

        count = 0;
    }

    /**
     * Reads the next byte.
     *
     * Reads the next byte of data from this input stream. The value byte
     * is returned as an int in the range 0 to 255. If no byte is available
     * because the end of the stream has been reached, the value -1 is
     * returned. This method blocks until input data is available, the end
     * of the stream is detected, or an exception is thrown.
     * If the bandwidth consumption exceeds the defined limit, read will block
     * until the bandwidth is in the limit again.
     *
     * @return the next byte from the stream or -1 if end-of-stream
     */
    public int read()
            throws IOException
    {
        long currentBandwidth;

        if (! isReading) {
            starttime = System.currentTimeMillis();
            isReading = true;
        }

        // do bandwidth check only if bandwidth
        if ((bandwidth > 0) &&
                ((count % CHECK_INTERVAL) == 0)) {
            do {
                usedtime = System.currentTimeMillis()-starttime;
                if (usedtime > 0) {
                    currentBandwidth = (count*1000) / usedtime;
                } else {
                    currentBandwidth = 0;
                }
                if (currentBandwidth > bandwidth) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            } while (currentBandwidth > bandwidth);
        }

        count++;
        return super.read();
    }

    /**
     * Shortcut for read(b,0,b.length)
     *
     * @see #read(byte[], int, int)
     */
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads a block of bytes from the stream.
     *
     * If the bandwith is not limited, it simply used the
     * read(byte[], int, int) method of the input stream, otherwise it
     * uses multiple read() request to enforce bandwith limitation (this
     * is easier to implement using byte reads).
     *
     * @return the number of bytes read or -1 at end of stream
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int mycount = 0;
        int current = 0;
        // limit bandwidth ?
        if (bandwidth > 0) {
            for (int i=off; i < off+len; i++) {
                current = read();
                if (current == -1) {
                    return mycount;
                } else {
                    b[i]=(byte)current;
                    count++;
                    mycount++;
                }
            }
            return mycount;
        } else {
            return in.read(b, off, len);
        }
    }

} // LimitedBandwidt

