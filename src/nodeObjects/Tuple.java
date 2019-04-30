package nodeObjects;

import java.util.ArrayList;


public class Tuple extends Operand {
    public ArrayList<TreeNodeObject> nodeOjects;
    
    public Tuple() {
        super("tup");
        this.nodeOjects = new ArrayList<TreeNodeObject>();
    }
}
