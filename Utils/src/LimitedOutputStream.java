import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LimitedOutputStream extends DataOutputStream {
    public LimitedOutputStream(OutputStream os){
        super(os);
    }
    public void writeLimited(byte arr[]) throws IOException {
        write(arr);
    }
}
