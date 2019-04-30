

import java.util.ArrayList;
import interpreter.Ast;
import interpreter.AstGenerator;
import interpreter.CseMachine;
import interpreter.InputScanner;
import interpreter.InputScannerGenerator;
import interpreter.ObjectGeneratorForCseMachine;
import interpreter.TreeNode;
import interpreter.TreeNodeGenerator;


public class myrpal {
    public static void main(String[] args) {        
        String streamOfData = args[0];                                                         
        InputScannerGenerator streamStringsCollector = new InputScannerGenerator();                       
        InputScanner StreamReader = streamStringsCollector.getFileReader(streamOfData);                                  
        ArrayList<String> stringStream = StreamReader.getData();                                         
        AstGenerator generator = new AstGenerator();       
        Ast abstractSyntaxTree = generator.getAst(stringStream);              
        abstractSyntaxTree.standardizeAst();                                                      
        ObjectGeneratorForCseMachine machineGenerator = new ObjectGeneratorForCseMachine();                      
        CseMachine machine = machineGenerator.getCseMachine(abstractSyntaxTree);                             
        System.out.println(machine.getOutput());                                   
    }
}
