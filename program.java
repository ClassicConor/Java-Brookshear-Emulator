import java.io.IOException;

public class program {
    public static void main(String[] args) throws IOException {

        String filename = "instructions.txt";
        PreProcessBrookshearInstructions preProcessBrookshearInstructions = new PreProcessBrookshearInstructions();
        preProcessBrookshearInstructions.processInstructions(filename);

        Assemble assembler = new Assemble(preProcessBrookshearInstructions.getLines());
        assembler.assemble();
    }
}