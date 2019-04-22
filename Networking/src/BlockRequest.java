public class BlockRequest {
    private int length;
    private int offset;
    private int index;

    public int getLength(){return length;}
    public int getOffset(){return offset;}
    public int getIndex(){return index;}

    public BlockRequest(int length,int offset,int index)
    {
        this.length=length; this.offset=offset; this.index=index;
    }
}
