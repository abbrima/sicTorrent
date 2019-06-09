import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Stack;

enum KVMode {
    KEY, VALUE
}

public class bCoder {
    static Integer i;
    static int firstIndex, lastIndex;

    private static KVMode toggle(KVMode m) {
        if (m == KVMode.KEY)
            return KVMode.VALUE;
        else
            return KVMode.KEY;
    }

    private static void validateKeyValueCombo(Parcel parcel, Stack<String> keys, String val) throws InvalidBencodeException {
        KeyDictionary.applyString(parcel, keys, val);
    }

    private static void validateKeyValueCombo(Parcel parcel, Stack<String> keys, long val) throws InvalidBencodeException {
        KeyDictionary.applyInteger(parcel, keys, val);
    }

    private static void validateKeyValueCombo(Parcel parcel, Stack<String> keys, byte[] val) throws InvalidBencodeException {
        KeyDictionary.applyBytes(parcel, keys, val);
    }

    private static String readStr(byte arr[]) throws InvalidBencodeException {
        String temp = new String();
        int length = 0;
        while (i < arr.length) {
            temp += (char) arr[i];
            try {
                length = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                if ((char) arr[i] == ':') {
                    i++;
                    break;
                } else {
                    System.out.println(arr[i]);
                    throw new InvalidBencodeException("Error reading length of string " + i + "\n" + new String(arr));
                }
            }
            i++;
        }
        temp = new String();
        byte tempb[] = new byte[length];
        for (int j = 0; j < length; j++) {
            tempb[j] = arr[i + j];
        }
        temp = new String(tempb, StandardCharsets.UTF_8);
        i += length;
        return temp;
    }

    private static byte[] readBytes(byte arr[]) throws InvalidBencodeException {
        String temp = new String();
        int length = 0;
        while (i < arr.length) {
            temp += (char) arr[i];
            try {
                length = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                if ((char) arr[i] == ':') {
                    i++;
                    break;
                } else
                    throw new InvalidBencodeException("Error reading length of string:\n" + i + new String(arr));
            }
            i++;
        }
        byte val[] = Arrays.copyOfRange(arr, i, i + length);
        i += length;
        return val;
    }

    private static long readInt(byte arr[]) throws InvalidBencodeException {
        String temp = new String();
        long val = 0;
        boolean negative = false;
        boolean firstChar = true;
        boolean isZero = false;
        while (i < arr.length) {
            temp += (char) arr[i];
            try {
                val = Long.parseLong(temp);
                if (val == 0)
                    if (isZero)
                        throw new InvalidBencodeException("Leading Zeros");
                    else
                        isZero = true;

            } catch (NumberFormatException e) {
                if ((char) arr[i] == 'e')
                    return val;
                else if ((char) arr[i] == '-') {
                    if (firstChar == true && negative == false) {
                        negative = true;
                        temp = new String();
                    } else
                        throw new InvalidBencodeException("Got Negative in incorrect place");
                } else {
                    System.out.println(i);
                    throw new InvalidBencodeException("Non Integer characters while reading int");
                }
            }
            i++;
        }
        return val;
    }

    private static boolean keyRequiresBytes(String key) {
        if (key.equals("pieces") || key.equals("peers"))
            return true;
        return false;
    }

    public synchronized static Parcel decode(byte arr[], ParcelType type) throws InvalidBencodeException {
        String tempKey = " ";
        Parcel parcel;
        if (type == ParcelType.TORRENT)
            parcel = new TorrentParcel();
        else if (type == ParcelType.RESPONSE)
            parcel = new ResponseParcel();
        else
            parcel = new ScrapeParcel();

        Stack<Character> stack = new Stack<>();
        Stack<String> KeyStack = new Stack<>();
        long IntValue;
        char c;
        KVMode kvmode = KVMode.VALUE;
        i = 0;
        KeyStack.push("MAIN");
        while (i < arr.length) {
            c = (char) arr[i];

            switch (c) {
                case 'i':
                    if (kvmode != KVMode.VALUE)
                        throw new InvalidBencodeException("Expected String Got Integer for key");
                    i++;
                    stack.push('i');
                    IntValue = readInt(arr);
                    validateKeyValueCombo(parcel, (Stack<String>) KeyStack.clone(), IntValue);
                    tempKey = KeyStack.pop();
                    kvmode = toggle(kvmode);


                    break;
                case 'l':
                    if (kvmode != KVMode.VALUE) throw new InvalidBencodeException("Expected String Got List for key");
                    stack.push('l');
                    kvmode = KVMode.VALUE;
                    i++;
                    break;

                case 'd':
                    if (kvmode != KVMode.VALUE)
                        throw new InvalidBencodeException("Expected String Got KeyDictionary for key");

                    if (KeyStack.peek().equals("info"))
                        firstIndex = i;
                    stack.push('d');
                    kvmode = toggle(kvmode);
                    i++;


                    break;
                case 'e':
                    if (stack.empty()) throw new InvalidBencodeException("Stack Empty and got E");
                    if (stack.peek() == 'd' && kvmode == KVMode.VALUE)
                        throw new InvalidBencodeException("KeyDictionary closed with a missing val");

                    if (stack.peek() == 'l') {
                        stack.pop();
                        if (stack.empty())
                            throw new InvalidBencodeException("Popped list but stack empty!");
                        if (stack.peek() != 'l') {
                            kvmode = KVMode.KEY;
                            tempKey = KeyStack.pop();
                            tempKey = "";
                        }


                    } else if (stack.peek() == 'd') {
                        stack.pop();
                        if (KeyStack.peek().equals("info"))
                            lastIndex = i + 1;
                        if (!stack.empty())
                            if (stack.peek() == 'l') kvmode = KVMode.VALUE;
                            else {
                                kvmode = KVMode.KEY;
                                tempKey = KeyStack.pop();
                            }
                    } else
                        stack.pop();

                    i++;

                    break;
                default:
                    if (stack.peek() == 'd')
                        switch (kvmode) {
                            case KEY:
                                String temp = new String();
                                temp = readStr(arr);
                                if (!(parcel instanceof ScrapeParcel) && !(parcel instanceof ResponseParcel) && temp.compareTo(tempKey) < 0)
                                    throw new InvalidBencodeException("Incorrect Lexicographical Order" + temp + "  " + tempKey);
                                KeyStack.push(temp);
                                kvmode = toggle(kvmode);
                                break;
                            case VALUE:
                                if (keyRequiresBytes(KeyStack.peek())) {
                                    validateKeyValueCombo(parcel, (Stack<String>) KeyStack.clone(), readBytes(arr));
                                    tempKey = KeyStack.pop();
                                    kvmode = toggle(kvmode);

                                } else {
                                    validateKeyValueCombo(parcel, (Stack<String>) KeyStack.clone(), readStr(arr));
                                    tempKey = KeyStack.pop();
                                    kvmode = toggle(kvmode);
                                }
                                break;
                        }
                    else if (stack.peek() == 'l') {
                        if (!KeyStack.peek().equals("path")) {
                            if (keyRequiresBytes(KeyStack.peek())) {
                                validateKeyValueCombo(parcel, (Stack<String>) KeyStack.clone(), readBytes(arr));
                            } else {
                                validateKeyValueCombo(parcel, (Stack<String>) KeyStack.clone(), readStr(arr));
                            }
                        } else {
                            String temp = new String();
                            do {
                                temp += readStr(arr) + "/";
                            }
                            while ((char) arr[i] != 'e');
                            temp = temp.substring(0,temp.length()-1);
                            validateKeyValueCombo(parcel,(Stack<String>)KeyStack.clone(),temp);
                        }
                    }
                    break;
            }

        }
        if (!stack.empty())
            throw new InvalidBencodeException("Array scanned but stack not empty!" + i);
        if (type == ParcelType.TORRENT) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-1");

                parcel.setInfoHash(md.digest(Arrays.copyOfRange(arr, firstIndex, lastIndex)));
            } catch (Exception ex) {
            }
        }
        return parcel;
    }
}

