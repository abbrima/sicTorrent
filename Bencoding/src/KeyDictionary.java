import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class KeyDictionary {
    private static HashMap<String, ValueType> keys;

   static{
        keys = new HashMap<>();
        keys.put("announce", ValueType.STRING);
        keys.put("announce-list", ValueType.STRING);
        keys.put("creation date", ValueType.INTEGER);
        keys.put("comment", ValueType.STRING);
        keys.put("created by", ValueType.STRING);
        keys.put("encoding", ValueType.STRING);
        keys.put("piece length", ValueType.INTEGER);
        keys.put("pieces", ValueType.BYTES);
        keys.put("private", ValueType.INTEGER);
        keys.put("name", ValueType.STRING);
        keys.put("length", ValueType.INTEGER);
        keys.put("md5sum", ValueType.STRING);
        keys.put("path", ValueType.STRING);
        keys.put("publisher", ValueType.STRING);
        keys.put("publisher-url", ValueType.STRING);
        keys.put("failure reason", ValueType.STRING);
        keys.put("warning message", ValueType.STRING);
        keys.put("interval", ValueType.INTEGER);
        keys.put("min interval", ValueType.INTEGER);
        keys.put("tracker id", ValueType.STRING);
        keys.put("complete", ValueType.INTEGER);
        keys.put("incomplete", ValueType.INTEGER);
        keys.put("peer id", ValueType.STRING);
        keys.put("ip", ValueType.STRING);
        keys.put("port", ValueType.INTEGER);
        keys.put("peers", ValueType.BYTES);
        keys.put("url-list", ValueType.STRING);
        keys.put("min_request_interval", ValueType.INTEGER);
        keys.put("downloaded", ValueType.INTEGER);
    }

    public static void applyString(Parcel parcel, Stack<String> keystack, String value) throws InvalidBencodeException {
        if (!keys.containsKey(keystack.peek())) {
            System.out.println("WARNING Unknown key: " + keystack.peek());
            return;
        }
        if (keys.get(keystack.peek()) != ValueType.STRING)
            throw new InvalidBencodeException("Incorrect value form for key: " + keystack.peek());
        switch (keystack.pop()) {

            case "announce":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setAnnounce(value);
                break;
            case "announce-list":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToAnnounceList(value);
                break;
            case "comment":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setComment(value);
                break;
            case "created by":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setCreatedBy(value);
                break;
            case "encoding":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setEncoding(value);
                break;


            case "name":

                if (parcel instanceof TorrentParcel && (keystack.empty() || (!keystack.pop().equals("info")) || (!keystack.pop().equals("MAIN"))))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                if (parcel instanceof ScrapeParcel && (keystack.empty() || keystack.pop()==null ||  (!keystack.pop().equals("files")) || (!keystack.pop().equals("MAIN"))))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setName(value);
                break;

            case "url-list":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToUrlList(value);
                break;
            case "path":
                if (keystack.pop().equals("files") && keystack.pop().equals("info") && keystack.pop().equals("MAIN")) {
                } else
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToPath(value);
                break;
            case "md5sum":
                if (keystack.empty() || (!keystack.peek().equals("info")) || (!keystack.peek().equals("files")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                keystack.pop();
                if (keystack.peek().equals("info")) {
                    keystack.pop();
                    if (!keystack.peek().equals("MAIN"))
                        throw new InvalidBencodeException("Invalid Hierarchy");

                } else if (!keystack.peek().equals("MAIN"))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToMd5sum(value);
                break;


            case "publisher":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setPublisher(value);
                break;
            case "publisher-url":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setPublisherURL(value);
                break;


            case "failure reason":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setFailureReason(value);
                break;

            case "warning message":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setWarningMessage(value);
                break;

            case "tracker id":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setTrackerID(value);
                break;

            case "peer id":
                if (keystack.empty() || (!keystack.pop().equals("peers")) || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToPeerID(value);
                break;

            case "ip":
                if (keystack.empty() || (!keystack.pop().equals("peers")) || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToPeerIP(value);
                break;

        }
    }

    public static void applyInteger(Parcel parcel, Stack<String> keystack, long value) throws InvalidBencodeException {
        if (!keys.containsKey(keystack.peek())) {
            System.out.println("WARNING Unknown key: " + keystack.peek());
            return;
        }
        if (keys.get(keystack.peek()) != ValueType.INTEGER)
            throw new InvalidBencodeException("Incorrect value form for key: " + keystack.peek());
        switch (keystack.pop()) {
            case "creation date":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setCreationDate((int) value);
                break;
            case "piece length":
                if (keystack.pop().equals("info") && keystack.pop().equals("MAIN")) {
                } else
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setPieceLength((int) value);
                break;
            case "downloaded":
                String hash;
                if (keystack.empty() || (hash=keystack.pop())==null || (!keystack.pop().equals("files")) || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setDownloaded((int) value); parcel.setUTF8Hash(hash);
                break;
            case "private":
                if (keystack.peek().equals("MAIN") && parcel instanceof ResponseParcel) {
                } else if (keystack.pop().equals("info") && keystack.pop().equals("MAIN")) {
                } else
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setPrivate((int) value);
                break;
            case "length":
                if (keystack.peek().equals("info") || keystack.peek().equals("files")) {
                    keystack.pop();
                    if (keystack.peek().equals("info")) {
                        keystack.pop();
                        if (keystack.peek().equals("MAIN")) {
                        } else
                            throw new InvalidBencodeException("Invalid Hierarchy");

                    } else if (keystack.peek().equals("MAIN")) {
                    } else
                        throw new InvalidBencodeException("Invalid Hierarchy");
                } else
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToLength(value);
                break;

            case "port":
                if (keystack.empty() || (!keystack.pop().equals("peers")) || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.addToPeerPort((int) value);
                break;

            case "interval":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setInterval((int) value);
                break;

            case "min interval":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setMinInterval((int) value);
                break;

            case "complete":
                if ( parcel instanceof ResponseParcel && (keystack.empty() || (!keystack.peek().equals("MAIN"))))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                if (parcel instanceof ScrapeParcel && (keystack.empty() || keystack.pop()==null || (!keystack.pop().equals("files")) || (!keystack.pop().equals("MAIN"))) )
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setComplete((int) value);
                break;

            case "incomplete":
                if ( parcel instanceof ResponseParcel && (keystack.empty() || (!keystack.peek().equals("MAIN"))) )
                    throw new InvalidBencodeException("Invalid Hierarchy");
                if (parcel instanceof ScrapeParcel && (keystack.empty() || keystack.pop()==null ||  (!keystack.pop().equals("files")) || (!keystack.pop().equals("MAIN"))) )
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setIncomplete((int) value);
                break;
            case "min_request_interval":
                if (parcel instanceof ScrapeParcel && (keystack.empty()  || (!keystack.pop().equals("flags")) || (!keystack.pop().equals("MAIN"))))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                parcel.setMinRequestInterval((int)value); break;

        }
    }

    public static void applyBytes(Parcel parcel, Stack<String> keystack, byte value[]) throws InvalidBencodeException {
        if (!keys.containsKey(keystack.peek())) {
            System.out.println("WARNING Unknown key: " + keystack.peek());
            return;
        }
        if (keys.get(keystack.peek()) != ValueType.BYTES)
            throw new InvalidBencodeException("Incorrect value form for key: " + keystack.peek());
        switch (keystack.pop()) {
            case "pieces":

                if (value.length % 20 != 0)
                    throw new InvalidBencodeException("Bytes for pieces not divisable by 20");
                ArrayList<byte[]> bytes = new ArrayList<>();
                for (int i = 0; i < value.length; i += 20)
                    bytes.add(Arrays.copyOfRange(value, i, i + 20));
                parcel.setHashValues(bytes);
                break;

            case "peers":
                if (keystack.empty() || (!keystack.pop().equals("MAIN")))
                    throw new InvalidBencodeException("Invalid Hierarchy");
                if (value.length % 6 != 0)
                    throw new InvalidBencodeException("Bytes for peers not divisable by 6");
                for (int i = 0; i < value.length; i += 6)
                    parcel.addToPeers(Arrays.copyOfRange(value, i, i + 6));
                break;
        }
    }
}
