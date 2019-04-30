package interpreter;

import java.util.ArrayList;

import nodeObjects.*;

public class ObjectGeneratorForCseMachine {
   
    private int lambdaIndex = 1;
    private int envIndex = 0;
    private Environment pe = new Environment(0);
    
    public ObjectGeneratorForCseMachine() {
        
    }
    
    public TreeNodeObject getObjectType(TreeNode node) {
      
            // unary operators
        if("not".equals(node.getInputStringStream()) || "+".equals(node.getInputStringStream()) || "-".equals(node.getInputStringStream()) || "*".equals(node.getInputStringStream())|| "/".equals(node.getInputStringStream())|| "**".equals(node.getInputStringStream())|| "&".equals(node.getInputStringStream())|| "or".equals(node.getInputStringStream()) || "eq".equals(node.getInputStringStream())|| "ne".equals(node.getInputStringStream())|| "ls".equals(node.getInputStringStream())|| "le".equals(node.getInputStringStream()) || "gr".equals(node.getInputStringStream()) || "ge".equals(node.getInputStringStream()) ) {
        	return new BinaryOperator(node.getInputStringStream());
        }
        else if("neg".equals(node.getInputStringStream())) {
        	return new UnaryOperator(node.getInputStringStream());
        }
        else if("aug".equals(node.getInputStringStream())) {
        	return new BinaryOperator(node.getInputStringStream());

        }
        else if("gamma".equals(node.getInputStringStream())) {
        	return new GammaNode();
        	
        }
        else if("tau".equals(node.getInputStringStream())) {
        	return new Tau(node.childNodes.size());
        }
        else if("ystar".equals(node.getInputStringStream())) {
        	return new Ystar();
        }
                         
        else {
            // operands <ID:>, <INT:>, <STR:>, <nil>, <true>, <false>, <dummy>
            
                if (node.getInputStringStream().startsWith("<ID:")) { 
                    return new Id(node.getInputStringStream().substring(4, node.getInputStringStream().length()-1));
                } else if (node.getInputStringStream().startsWith("<INT:")) {                    
                    return new IntegerObject(node.getInputStringStream().substring(5, node.getInputStringStream().length()-1));
                } else if (node.getInputStringStream().startsWith("<STR:")) {                    
                    return new StringObject(node.getInputStringStream().substring(6, node.getInputStringStream().length()-2));
                } else if (node.getInputStringStream().startsWith("<nil")) {                    
                    return new Tuple();
                } else if (node.getInputStringStream().startsWith("<true>")) {                    
                    return new BooleanObjects("true");
                } else if (node.getInputStringStream().startsWith("<false>")) {                    
                    return new BooleanObjects("false");
                } else if (node.getInputStringStream().startsWith("<dummy>")) {                    
                    return new Dummy();
                } else {
                    System.out.println("Err node: "+node.getInputStringStream());
                    return new Err();
                }  
        }
        
        }
    
    
    public B getB(TreeNode node) {
        B b = new B();
        b.nodeObjects = this.getPreOrderTraverse(node);
        return b;
    }
    
    public LambdaNode getLambdaNode(TreeNode node) {
        LambdaNode lambdaNode = new LambdaNode(this.lambdaIndex++);
        lambdaNode.setDelta(this.getDeltaNode(node.childNodes.get(1)));
        if (",".equals(node.childNodes.get(0).getInputStringStream())) {
            for (TreeNode identifier: node.childNodes.get(0).childNodes) {
                lambdaNode.identifiers.add(new Id(identifier.getInputStringStream().substring(4, node.getInputStringStream().length()-1)));
            }
        } else {
            lambdaNode.identifiers.add(new Id(node.childNodes.get(0).getInputStringStream().substring(4, node.getInputStringStream().length()-1)));
        }
        return lambdaNode;
    }
    
    private ArrayList<TreeNodeObject> getPreOrderTraverse(TreeNode node) {
        ArrayList<TreeNodeObject> nodeObjects = new ArrayList<TreeNodeObject>();
        if ("lambda".equals(node.getInputStringStream())) {
            nodeObjects.add(this.getLambdaNode(node));
        } else if ("->".equals(node.getInputStringStream())) {
            nodeObjects.add(this.getDeltaNode(node.childNodes.get(1)));
            nodeObjects.add(this.getDeltaNode(node.childNodes.get(2)));
            nodeObjects.add(new Beta());
            nodeObjects.add(this.getB(node.childNodes.get(0)));
        } else {
            nodeObjects.add(this.getObjectType(node));
            for (TreeNode child: node.childNodes) {
                nodeObjects.addAll(this.getPreOrderTraverse(child));
            }
        }
        return nodeObjects;
    }
    
    public Delta getDeltaNode(TreeNode node) {
        Delta delta_node = new Delta(this.envIndex++);
        delta_node.nodeObjects = this.getPreOrderTraverse(node);
        return delta_node;        
    }

    public ArrayList<TreeNodeObject> getCurrentStack() {
        ArrayList<TreeNodeObject> stack = new ArrayList<TreeNodeObject>();
        stack.add(this.pe);
        return stack;
    }
    
    public ArrayList<Environment> getEnvironment() {
        ArrayList<Environment> env = new ArrayList<Environment>();
        env.add(this.pe);
        return env;
    }
    
    public ArrayList<TreeNodeObject> getControlStructure(Ast ast) {
        ArrayList<TreeNodeObject> control = new ArrayList<TreeNodeObject>();
        control.add(this.pe);
        control.add(this.getDeltaNode(ast.getRootNode()));
        return control;
    }
    
    
    public CseMachine getCseMachine(Ast ast) {        
        return new CseMachine(this.getControlStructure(ast), this.getCurrentStack(), this.getEnvironment());
    }
}
