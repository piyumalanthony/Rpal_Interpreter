package interpreter;


import java.util.ArrayList;

public class AstGenerator {
    
    public AstGenerator() {
        
    }
    
    public Ast getAst(ArrayList<String> stringStream) {
        TreeNode root_node = TreeNodeGenerator.getNode(stringStream.get(0), 0);
        TreeNode parent_node = root_node;
        int node_depth = 0;
        
        for (String s: stringStream.subList(1, stringStream.size())) {
            int current_index = 0;                                                        
            int current_depth = 0;                                                         
            
            while (s.charAt(current_index) == '.') { 
                current_depth++; 
                current_index++; 
            }            
            
            TreeNode current_node = TreeNodeGenerator.getNode(s.substring(current_index), current_depth); 
            
            if (node_depth < current_depth) {
                parent_node.childNodes.add(current_node);
                current_node.setParentNode(parent_node);               
            } else {
                while (parent_node.getDepthOfNode() != current_depth) {
                    parent_node = parent_node.getParentNode();
                }
                parent_node.getParentNode().childNodes.add(current_node);
                current_node.setParentNode(parent_node.getParentNode());
            }
            
            parent_node = current_node;
            node_depth = current_depth;
        }        
        return new Ast(root_node);
    }
}
