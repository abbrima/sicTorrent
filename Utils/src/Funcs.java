import java.util.Iterator;

public class Funcs {
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static byte[] getByteByString(String binaryString) {
        byte arr[] = new byte[binaryString.length() / 8];
        for (int i = 0; i < binaryString.length(); i += 8) {
            String s = binaryString.substring(i, i + 8);
            byte b = 0;
            for (int j = 7; j > 0; j--) {
                char c = s.charAt(j);
                try {
                    b += Integer.valueOf(""+c) * Math.pow(2,Math.abs(7-j));
                } catch (Exception e) {
                    System.exit(-1);
                }
            }
            if (s.charAt(0)=='1')
                b*=-1;
            arr[i / 8] = b;
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
}
