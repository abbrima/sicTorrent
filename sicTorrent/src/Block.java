import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Block implements Serializable {
    private int index;
    private int offset;
    private int length;




    public Block(int index, int length, int offset) {
        this.index = index;
        this.length = length;
        this.offset = offset;

    }





    public int getOffset() {
        return offset;
    }

    public int getIndex() {
        return index;
    }

    public int getLength() {
        return length;
    }

}
