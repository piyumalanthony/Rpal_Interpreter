package interpreter;

public class InputScannerGenerator {
    public InputScannerGenerator() {
        
    }
    
    // returen new scanner to read data
    public InputScanner getFileReader(String inputStringStream) {
        return new InputScanner(inputStringStream);
    }
}
