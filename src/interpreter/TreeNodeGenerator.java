package interpreter;

import java.util.ArrayList;

public class TreeNodeGenerator {
    
    public TreeNodeGenerator() {
        
    }
    //create a node for the tree with given set of attributes, to create a tree, the basic properties used for nodes are the depth, left child and right child
    public static TreeNode getNode(String stream, int depthOfNode) {
        TreeNode treeNode = new TreeNode();
        treeNode.setInputStringStream(stream);
        treeNode.setDepthOfNode(depthOfNode);
        treeNode.childNodes = new ArrayList<TreeNode>();
        return treeNode;
    }
    // overide the above method
    public static TreeNode getNode(String stream, int depthOfNode, TreeNode parent_Node, ArrayList<TreeNode> childNodes, boolean stdCompleteness) {
        TreeNode treeNode = new TreeNode();
        treeNode.setInputStringStream(stream);
        treeNode.setDepthOfNode(depthOfNode);
        treeNode.setParentNode(parent_Node);
        treeNode.childNodes = childNodes;
        treeNode.stdCompleteness = stdCompleteness;
        return treeNode;
    }
}
