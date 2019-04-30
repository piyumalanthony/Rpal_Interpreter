package interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class InputScanner {
	private ArrayList<String> inputStream;
    private final String inputFile;
    private Scanner scanner;
   
    
    public InputScanner(String fileName) {
        this.inputFile = fileName;
    }
    
    private void getSourceStream() {
        try {
            this.scanner = new Scanner(new File(this.inputFile));
        } catch(Exception e) {
            System.out.println("file does not exist.");
        }
    }
    
    private void readSourceString() {
        this.inputStream = new ArrayList<String>();
        while(this.scanner.hasNextLine()) {
             this.inputStream.add(this.scanner.nextLine());
        }
    }
    
    private void closeStream() {
        this.scanner.close();
    }
    
    public ArrayList<String> getData() {
        this.getSourceStream();
        this.readSourceString();
        this.closeStream();
        return this.inputStream;
    }
}