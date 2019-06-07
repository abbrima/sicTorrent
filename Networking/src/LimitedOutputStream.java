import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LimitedOutputStream extends DataOutputStream {
    private BandwidthController controller;

    public LimitedOutputStream(OutputStream os){
        super(os);
    }
    public void writeLimited(byte arr[]) throws IOException {
        int kbps = controller.requestUpBandwidth();
        if (kbps<0)
             write(arr);
        else if (kbps>0)
        {

        }
    }
    public void setController(BandwidthController controller){
        this.controller=controller;
        this.controller=controller;
    }
}
