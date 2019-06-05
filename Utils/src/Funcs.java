import java.util.Iterator;

public class Funcs {
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
    public static byte[] hexToByteArray(String hex){
        byte arr[] = new byte[hex.length()/2];
        for (int i=0;i<hex.length();i+=2)
        {
            int s = Integer.parseInt(hex.substring(i,i+2),16);

            arr[i/2] = (byte)s;
        }
        return arr;
    }

    public static byte[] getByteByString(String binaryString) {
        byte arr[] = new byte[binaryString.length() / 8];
        for (int i = 0; i < binaryString.length(); i += 8) {
            String s = binaryString.substring(i, i + 8);
            Integer byt = Integer.parseInt(s,2);
            arr[i / 8] = byt.byteValue();
        }
        return arr;
    }

    public static String toBitString(final byte[] b) {
        final char[] bits = new char[8 * b.length];
        for (int i = 0; i < b.length; i++) {
            final byte byteval = b[i];
            int bytei = i << 3;
            int mask = 0x1;
            for (int j = 7; j >= 0; j--) {
                final int bitval = byteval & mask;
                if (bitval == 0) {
                    bits[bytei + j] = '0';
                } else {
                    bits[bytei + j] = '1';
                }
                mask <<= 1;
            }
        }
        return String.valueOf(bits);
    }
    public static int bytetoint(byte [] b)
    {
        return b[0] << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
    }
}
