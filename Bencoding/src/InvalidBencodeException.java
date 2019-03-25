public class InvalidBencodeException extends Exception {
    public InvalidBencodeException(String s){
        super(s);
    }
    public InvalidBencodeException(){super("DEFAULT MSG");}
}
