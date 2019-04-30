package interpreter;

import java.util.ArrayList;

public class TreeNode {
    private String input_string_stream; // to generate a tree a node basically have three attributes named depthOfNode, left child and right child
    private int depthOfNode;
    private TreeNode parent_node;
    public ArrayList<TreeNode> childNodes;    
    public boolean stdCompleteness = false;
    
    public TreeNode() {
        
    }
    public void setDepthOfNode(int depthOfNode) {
        this.depthOfNode = depthOfNode;
    }
    
    public int getDepthOfNode() {
        return this.depthOfNode;
    }
    
    public void setParentNode(TreeNode parent_node) {
        this.parent_node = parent_node;
    }
    
    public TreeNode getParentNode() {
        return this.parent_node;
    }
    
    public void setInputStringStream(String stream) {
        this.input_string_stream = stream;
    }
    
    public String getInputStringStream() {
        return this.input_string_stream;
    }
    
    public int getChildrenDegree() {
        return childNodes.size();
    }
    
    
    
    public void standardizeAst() { //standardize ast
        if (!this.stdCompleteness) {
            for (TreeNode child: this.childNodes) {// iterare through every node in ast
                child.standardizeAst();
            }
            if("let".equals(this.getInputStringStream())) {
                // standardizing let
                
                    TreeNode firstTreeNode = this.childNodes.get(0).childNodes.get(1);
                    firstTreeNode.setParentNode(this);
                    firstTreeNode.setDepthOfNode(this.depthOfNode+1);
                    TreeNode secondTreeNode = this.childNodes.get(1);                    
                    secondTreeNode.setParentNode(this.childNodes.get(0));
                    secondTreeNode.setDepthOfNode(this.depthOfNode+2);
                    this.childNodes.set(1, firstTreeNode);
                    this.childNodes.get(0).setInputStringStream("lambda");
                    this.childNodes.get(0).childNodes.set(1, secondTreeNode);
                    this.setInputStringStream("gamma");
                    
            }
            else if("where".equals(this.getInputStringStream())) {
                // standardizing where
                    TreeNode treeNode = this.childNodes.get(0);
                    this.childNodes.set(0, this.childNodes.get(1));
                    this.childNodes.set(1, treeNode);
                    this.setInputStringStream("let");
                    this.standardizeAst();
                    
            }
            else if("function_form".equals(this.getInputStringStream())) {
                // standardizing function_form
                    TreeNode existingVar = this.childNodes.get(this.childNodes.size()-1);                    
                    TreeNode lambdaNew = TreeNodeGenerator.getNode("lambda", this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                    this.childNodes.add(1, lambdaNew);
                    while (!this.childNodes.get(2).equals(existingVar)) {
                        TreeNode V = this.childNodes.get(2);
                        this.childNodes.remove(2);
                        V.setDepthOfNode(lambdaNew.depthOfNode+1);
                        V.setParentNode(lambdaNew);
                        lambdaNew.childNodes.add(V);
                        if (this.childNodes.size() > 3) {
                            lambdaNew = TreeNodeGenerator.getNode("lambda", lambdaNew.depthOfNode+1, lambdaNew, new ArrayList<TreeNode>(), true);
                            lambdaNew.getParentNode().childNodes.add(lambdaNew);
                        }
                    }
                    lambdaNew.childNodes.add(existingVar);
                    this.childNodes.remove(2);                    
                    this.setInputStringStream("=");
                   
            }
            else if("lambda".equals(this.getInputStringStream())) {
                // standardizing lambda (multi parameter functions)
                    if (this.childNodes.size() > 2) {
                        TreeNode existingVar = this.childNodes.get(this.childNodes.size()-1);
                        TreeNode newLambda = TreeNodeGenerator.getNode("lambda", this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                        this.childNodes.add(1, newLambda);
                        while (!this.childNodes.get(2).equals(existingVar)) {
                            TreeNode secondChild = this.childNodes.get(2);
                            this.childNodes.remove(2);
                            secondChild.setDepthOfNode(newLambda.depthOfNode+1);
                            secondChild.setParentNode(newLambda);
                            newLambda.childNodes.add(secondChild);
                            if (this.childNodes.size() > 3) {
                                newLambda = TreeNodeGenerator.getNode("lambda", newLambda.depthOfNode+1, newLambda, new ArrayList<TreeNode>(), true);
                                newLambda.getParentNode().childNodes.add(newLambda);
                            }
                        }
                        newLambda.childNodes.add(existingVar);
                        this.childNodes.remove(2);
                    }
                    
            }
            else if("within".equals(this.getInputStringStream())) {
                // standardizing within
                
                    TreeNode X1_node = this.childNodes.get(0).childNodes.get(0);                    
                    TreeNode X2_node = this.childNodes.get(1).childNodes.get(0);
                    TreeNode E1_node = this.childNodes.get(0).childNodes.get(1);
                    TreeNode E2_node = this.childNodes.get(1).childNodes.get(1);
                    TreeNode newlambda = TreeNodeGenerator.getNode("lambda", this.depthOfNode+2, this.childNodes.get(1), new ArrayList<TreeNode>(), true);                    
                    X1_node.setDepthOfNode(X1_node.depthOfNode+1);
                    X1_node.setParentNode(newlambda);                    
                    X2_node.setDepthOfNode(X1_node.depthOfNode-1);
                    X2_node.setParentNode(this);                    
                    E1_node.setDepthOfNode(E1_node.depthOfNode);
                    E1_node.setParentNode(this.childNodes.get(1));                    
                    E2_node.setDepthOfNode(E2_node.depthOfNode+1);
                    E2_node.setParentNode(newlambda);                    
                    newlambda.childNodes.add(X1_node);
                    newlambda.childNodes.add(E2_node);                    
                    this.childNodes.get(1).setInputStringStream("gamma");
                    this.childNodes.get(1).childNodes.clear();
                    this.childNodes.get(1).childNodes.add(newlambda);
                    this.childNodes.get(1).childNodes.add(E1_node); 
                    this.setInputStringStream("=");
                    
            }
            else if("@".equals(this.getInputStringStream())) {
                // standardizing @
                    TreeNode newgamma = TreeNodeGenerator.getNode("gamma", this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                    TreeNode env1 = this.childNodes.get(0);
                    env1.setDepthOfNode(env1.getDepthOfNode()+1);
                    env1.setParentNode(newgamma);
                    TreeNode n = this.childNodes.get(1);
                    n.setDepthOfNode(n.getDepthOfNode()+1);
                    n.setParentNode(newgamma);
                    newgamma.childNodes.add(n);
                    newgamma.childNodes.add(env1);
                    this.childNodes.remove(0);
                    this.childNodes.remove(0);
                    this.childNodes.add(0, newgamma);                    
                    this.setInputStringStream("gamma");
                    
            }
            else if("and".equals(this.getInputStringStream())) {
                // standardizing and (simultaneous definitions)
              
                    TreeNode comma_node = TreeNodeGenerator.getNode(",", this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                    TreeNode tau_node = TreeNodeGenerator.getNode("tau",this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                    for (TreeNode equal: this.childNodes) {
                        equal.childNodes.get(0).setParentNode(comma_node);
                        equal.childNodes.get(1).setParentNode(tau_node);
                        comma_node.childNodes.add(equal.childNodes.get(0));
                        tau_node.childNodes.add(equal.childNodes.get(1));
                    }
                    this.childNodes.clear();
                    this.childNodes.add(comma_node);
                    this.childNodes.add(tau_node);
                    this.setInputStringStream("=");
                    
            }
            else if("rec".equals(this.getInputStringStream())) {
                // standardizing rec
               
                    TreeNode X_node = this.childNodes.get(0).childNodes.get(0);
                    TreeNode E_node = this.childNodes.get(0).childNodes.get(1);
                    TreeNode F_node = TreeNodeGenerator.getNode(X_node.getInputStringStream(), this.depthOfNode+1, this, X_node.childNodes, true);
                    TreeNode G_node = TreeNodeGenerator.getNode("gamma", this.depthOfNode+1, this, new ArrayList<TreeNode>(), true);
                    TreeNode Y_node = TreeNodeGenerator.getNode("ystar", this.depthOfNode+2, G_node, new ArrayList<TreeNode>(), true);
                    TreeNode L_node = TreeNodeGenerator.getNode("lambda", this.depthOfNode+2, G_node, new ArrayList<TreeNode>(), true);                    
                    X_node.setDepthOfNode(L_node.depthOfNode+1);
                    X_node.setParentNode(L_node);
                    E_node.setDepthOfNode(L_node.depthOfNode+1);
                    E_node.setParentNode(L_node);
                    L_node.childNodes.add(X_node);
                    L_node.childNodes.add(E_node);
                    G_node.childNodes.add(Y_node);
                    G_node.childNodes.add(L_node);
                    this.childNodes.clear();
                    this.childNodes.add(F_node);
                    this.childNodes.add(G_node);
                    this.setInputStringStream("=");
                    
            }
                                 
            }
        
        this.stdCompleteness = true;
    }
}
