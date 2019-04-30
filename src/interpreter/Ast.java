package interpreter;

public class Ast {
    private TreeNode rootNode;
    
    public Ast(TreeNode root) {
        this.setRootNode(root);
    }
    
    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }
    
    public TreeNode getRootNode() {
        return this.rootNode;
    }
    
    public void standardizeAst() {  
        if (!this.rootNode.stdCompleteness) {
            this.rootNode.standardizeAst();
        }
    }
    
    private void preOrderTraverse(TreeNode node,int i) {
        for (int n = 0; n < i; n++) {System.out.print(".");}
        System.out.println(node.getInputStringStream());
        node.childNodes.forEach((child) -> preOrderTraverse(child, i+1));
    }
    
    public void printAst() {
        this.preOrderTraverse(this.getRootNode(), 0);
    }
}
