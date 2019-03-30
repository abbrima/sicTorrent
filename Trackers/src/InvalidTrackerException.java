public class InvalidTrackerException extends Exception {
    public InvalidTrackerException()    {super("DEFAULT MSG");}
    private boolean exist;
    public boolean exists(){return exist;}
    public InvalidTrackerException(String s){super(s);}
    public InvalidTrackerException(boolean b){this.exist=b;}
}
