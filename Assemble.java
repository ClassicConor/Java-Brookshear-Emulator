import java.util.List;

public class Assemble {

    private List<String> instructions;
    private String[] memory;

    public Assemble(List<String> instructions) {
        this.instructions = instructions;
        this.memory = new String[256];
    }

    public void assemble() {

        int j = 0;

        for (int i = 0; i < instructions.size(); i++) {
            String opcodeOperand[] = new String[2];
            String line = instructions.get(i);

            if (line.contains("HALT")) {
                opcodeOperand = instructionHalt();
                System.out.println(opcodeOperand[0] + " " + opcodeOperand[1]);
            } else if (line.contains("NOP")) {
                opcodeOperand = instructionNOP();
            } else if (line.contains("OR")) {
                opcodeOperand = instructionLogical(line, "OR");
            } else if (line.contains("AND")) {
                opcodeOperand = instructionLogical(line, "AND");
            } else if (line.contains("XOR")) {
                opcodeOperand = instructionLogical(line, "XOR");
            } else if (line.contains("ROT")) {
                opcodeOperand = instructionRotate(line);
            } else if (line.contains("ADDI")) {
                opcodeOperand = instructionAdd(line, "I");
            } else if (line.contains("ADDF")) {
                opcodeOperand = instructionAdd(line, "F");
            } else if (line.contains("DATA")) {
                opcodeOperand = instructionData(line);
            } else if (line.contains("JMPLT")) {
                opcodeOperand = instructionJumpConditional(line, "LT");
            } else if (line.contains("JMPGT")) {
                opcodeOperand = instructionJumpConditional(line, "GT");
            } else if (line.contains("JMPLE")) {
                opcodeOperand = instructionJumpConditional(line, "LE");
            } else if (line.contains("JMPGE")) {
                opcodeOperand = instructionJumpConditional(line, "GE");
            } else if (line.contains("JMPNE")) {
                opcodeOperand = instructionJumpConditional(line, "NE");
            } else if (line.contains("JMPEQ")) {
                opcodeOperand = instructionJumpEQ(line);
            } else if (line.contains("JMP")) {
                opcodeOperand = instructionJump(line);
            }

            memory[j] = opcodeOperand[0];
            memory[j + 1] = opcodeOperand[1];
            j += 2;
        }
        printMemory();
    }

    public String[] instructionHalt() {
        return new String[] { "C0", "00" };
    }

    public String[] instructionNOP() {
        return new String[] { "0F", "FF" };
    }

    public String[] instructionLogical(String line, String code) {
        StringBuilder opcode = new StringBuilder();
        StringBuilder operand = new StringBuilder();

        switch (code) {
            case "OR":
                opcode.append("7");
                break;
            case "AND":
                opcode.append("8");
                break;
            case "XOR":
                opcode.append(9);
                break;
        }

        String[] parts = line.split("R");
        opcode.append(parts[1].charAt(0));
        operand.append(parts[3].charAt(0)).append(parts[2].charAt(0));
        return new String[] { opcode.toString(), operand.toString() };
    }

    public String[] instructionRotate(String line) {
        StringBuilder opcode = new StringBuilder("A");
        StringBuilder operand = new StringBuilder("0");
        opcode.append(line.charAt(4));
        operand.append(line.charAt(6));
        return new String[] { opcode.toString(), operand.toString() };
    }

    public String[] instructionAdd(String line, String mode) {

        StringBuilder opcode = new StringBuilder("");
        StringBuilder operand = new StringBuilder("");

        switch (mode) {
            case "I":
                opcode.append("5");
                break;
            case "F":
                opcode.append("6");
                break;
            default:
                System.out.println("Must be either I or F mode.");
                break;
        }

        opcode.append(line.charAt(12));
        operand.append(line.charAt(5)).append(line.charAt(8));

        return new String[] { opcode.toString(), operand.toString() };
    }

    public String[] instructionData(String line) {
        line = line.replace("DATA", "");
        if (line.length() != 2) {
            line = "0" + line;
        }
        return new String[] { line, "00" };
    }

    public String[] instructionJumpConditional(String line, String code) {
        StringBuilder opcode = new StringBuilder("F");
        StringBuilder operand = new StringBuilder("");

        switch (code) {
            case "LT":
                operand.append("5");
                break;
            case "GT":
                operand.append("4");
                break;
            case "LE":
                operand.append("3");
                break;
            case "GE":
                operand.append("2");
                break;
            case "NE":
                operand.append("1");
                break;
            default:
                System.out.println("Invalid code.");
                break;
        }
        line = line.replace("JMP" + code + "R", "");
        opcode.append(line.charAt(0));
        operand.append(line.charAt(3));

        return new String[] { opcode.toString(), operand.toString() };
    }

    public String[] instructionJumpEQ(String line) {
        StringBuilder opcode = new StringBuilder("");
        StringBuilder operand = new StringBuilder("");

        if (line.contains("JMPEQR")) {
            line = line.replace("JMPEQR", "");
            String[] parts = line.split(",R");
            opcode.append("F").append(parts[1]);
            operand.append("0").append(parts[0]);
            System.out.println("opcode and operand:" + opcode.toString() + " " + operand.toString());
        } else {
            line = line.replace("JMPEQ", "");
            String[] parts = line.split(",R");
            opcode.append("B").append(parts[1]);
            operand.append(parts[0]);

            System.out.println("opcode and operand:" + opcode.toString() + " " + operand.toString());
        }

        return new String[] { opcode.toString(), operand.toString() };
    }

    // Not complete
    public String[] instructionJump(String line) {
        StringBuilder opcode = new StringBuilder("");
        StringBuilder operand = new StringBuilder("");

        if (line.contains("JMPR")) {
            line = line.replace("JMPR", "");
            opcode.append("F0");
            // operand.append("0").append(line.charAt(0));
        } else {
            line = line.replace("JMP", "");
            opcode.append("B0");
            // operand.append("0").append(line.charAt(3));
        }

        return new String[] { opcode.toString(), operand.toString() };
    }

    public void printMemory() {
        for (int i = 0; i < memory.length; i++) {
            if (memory[i] != null) {
                System.out.println(i + ": " + memory[i]);
            }
        }
    }

}
