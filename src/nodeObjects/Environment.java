package nodeObjects;

import java.util.HashMap;

public class Environment extends TreeNodeObject {
    private int index;
    private Environment parent;
    private boolean checkRemoved = false;
    public HashMap<Id,TreeNodeObject> values;
    
    public Environment(int index) {
        super("env");
        this.setIndex(index);
        this.values = new HashMap<Id,TreeNodeObject>();
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setParentenv(Environment env) {
        this.parent = env;
    }
    
    public Environment getParentenv() {
        return this.parent;
    }
    
    public void setIsRemoved(boolean checkRemoved) {
        this.checkRemoved = checkRemoved;
    }
    
    public boolean checkIsRemoved() {
        return this.checkRemoved;
    }
    
    public TreeNodeObject fetchData(Id id){
        for (Id key: this.values.keySet()) {
            if (key.getInfo().equals(id.getInfo())){
                return this.values.get(key);
            }
        }
        if (this.parent != null) {
            return this.parent.fetchData(id);
        } else {
            return new TreeNodeObject(id.getInfo());
        }
    }
}
