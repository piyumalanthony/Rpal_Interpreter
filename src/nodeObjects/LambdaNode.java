package nodeObjects;

import java.util.ArrayList;

public class LambdaNode extends TreeNodeObject {
    private int index;
    private int environment;
    public ArrayList<Id> identifiers;
    private Delta delta;
    
    public LambdaNode(int i) {
        super("lambda");
        this.setIndex(i);
        this.identifiers = new ArrayList<Id>();
    }
    
    private void setIndex(int i) {
        this.index = i;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setEnvironment(int n) {
        this.environment = n;
    }
    
    public int getEnvironment() {
        return this.environment;
    }
    
    public void setDelta(Delta delta) {
        this.delta = delta;
    }
    
    public Delta getDelta() {
        return this.delta;
    }
}
