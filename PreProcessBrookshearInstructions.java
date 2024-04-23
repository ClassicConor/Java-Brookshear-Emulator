import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreProcessBrookshearInstructions {

    private List<String> lines;
    private HashMap<String, Byte> labelAndPosition;

    public PreProcessBrookshearInstructions() {
        this.labelAndPosition = new HashMap<>();
    }

    public void processInstructions(String fileName) throws IOException {
        readAllLines(fileName);
        removeWhiteSpace();
        removeComments();
        removeEmptyLines();
        removeLabelDefinitions();
        replaceLabelUse();
        printAll();
    }

    public void readAllLines(String fileName) throws IOException {
        this.lines = Files.readAllLines(Paths.get(fileName));
    }

    /**
     * 1 - Go through each line and get rid of all white spaces
     */
    public void removeWhiteSpace() {

        // Old method:

        // for (int i = 0; i < lines.size(); i++) {
        // StringBuilder stringBuilder = new StringBuilder();
        // for (char c : lines.get(i).toCharArray()) {
        // if (c != ' ') {
        // stringBuilder.append(c);
        // }
        // }
        // lines.set(i, stringBuilder.toString());
        // }

        // New method
        lines.replaceAll(line -> line.replaceAll("\\s", ""));

    }

    /**
     * 2 - Remove all the comments within the code
     */
    public void removeComments() {

        // Old method:

        // for (int i = 0; i < lines.size(); i++) {
        // if (lines.get(i).contains("//")) {
        // lines.set(i, lines.get(i).substring(0, lines.get(i).indexOf("//")));
        // }
        // }

        // New method:
        lines.replaceAll(line -> {
            int index = line.indexOf("//");
            return index != -1 ? line.substring(0, index) : line;
        });
    }

    /**
     * 3 - Remove all empty lines from our list
     */
    public void removeEmptyLines() {

        // Old method:
        // Iterator<String> it = lines.iterator();

        // while (it.hasNext()) {
        // String line = it.next();
        // if (line.isBlank()) {
        // it.remove();
        // }
        // }
        // }

        // New Method:
        lines.removeIf(String::isBlank);

    }

    /**
     * 4 - Remove all label definitions and save their position to a key-value pair
     */
    public void removeLabelDefinitions() {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String label = line.substring(0, colonIndex);
                byte value = (byte) (i * 2);
                labelAndPosition.put(label, value);
                lines.set(i, line.substring(colonIndex + 1));
            }
        }
    }

    /**
     * 5 - Replace all the labels in the program with
     */
    public void replaceLabelUse() {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (Map.Entry<String, Byte> entry : labelAndPosition.entrySet()) {
                String label = entry.getKey();
                byte value = entry.getValue();
                line = line.replaceAll(label, Byte.toString(value));
            }
            lines.set(i, line);
        }
    }

    /**
     * Print out all the lines
     */
    public void printAll() {
        for (int i = 0; i < lines.size(); i++)
            System.out.println((i * 2) + ": " + lines.get(i));
    }

    public List<String> getLines() {
        return lines;
    }
}
