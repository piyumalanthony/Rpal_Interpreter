package nodeObjects;


import java.util.ArrayList;

public class Delta extends TreeNodeObject {
    private int index;
    public ArrayList<TreeNodeObject> nodeObjects;
    
    public Delta(int i) {
        super("delta");
        this.setIndex(i);
    }
    
    private void setIndex(int i) {
        this.index = i;
    }
    
    public int getIndex() {
        return this.index;
    }    
}
