package nodeObjects;

public class Eta extends TreeNodeObject {
    private int index;
    private int environment;
    private Id identifier;
    private LambdaNode lambda;
    
    public Eta() {
        super("eta");
    }    
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setEnvironment(int env) {
        this.environment = env;
    }
    
    public int getEnvironment() {    
        return this.environment;
    }
    
    public void setIdentifier(Id id) {
        this.identifier = id;
    }
    
    public void setLambda(LambdaNode lambda) {
        this.lambda = lambda;
    }
    
    public LambdaNode getLambda() {
        return this.lambda;
    }
    
}
