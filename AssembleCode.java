import java.util.List;

public class AssembleCode {

    private byte[] byteArray;
    private List<String> instructions;

    public AssembleCode(List<String> instructions) {
        this.byteArray = new byte[256];
        this.instructions = instructions;
    }

    public void assemble() {
        int j = 0;

        for (int i = 0; i < instructions.size(); i++) {
            // System.out.println(instructions.get(i));
            byte[] opcodeAndOperand = new byte[2];
            String line = instructions.get(i);

            opcodeAndOperand[0] = 0;
            opcodeAndOperand[1] = 0;

            if (line.contains("NOP")) {
                opcodeAndOperand[0] = (byte) 0x0F;
                opcodeAndOperand[1] = (byte) 0xFF;
            } else if (line.contains("MOV")) {
                opcodeAndOperand = instructionMove(line);
            } else if (line.contains("HALT")) {
                opcodeAndOperand[0] = (byte) 0xC0;
                opcodeAndOperand[1] = (byte) 0x00;
            } else if (line.contains("ROT")) {
                opcodeAndOperand = instructionRotate(line);
            } else if (line.contains("ADDI")) {
                opcodeAndOperand = instructionAdd(line, "I");
            } else if (line.contains("ADDF")) {
                opcodeAndOperand = instructionAdd(line, "F");
            } else if (line.contains("OR")) {
                opcodeAndOperand = instructionLogical(line, "OR");
            } else if (line.contains("AND")) {
                opcodeAndOperand = instructionLogical(line, "AND");
            } else if (line.contains("XOR")) {
                opcodeAndOperand = instructionLogical(line, "XOR");
            } else if (line.contains("JMPEQ")) {
                opcodeAndOperand = instructionJumpEQ(line);
            } else if (line.contains("JMPNE")) {
                opcodeAndOperand = instructionJumpConditional(line, "NE");
            } else if (line.contains("JMPGE")) {
                opcodeAndOperand = instructionJumpConditional(line, "GE");
            } else if (line.contains("JMPLE")) {
                opcodeAndOperand = instructionJumpConditional(line, "LE");
            } else if (line.contains("JMPGT")) {
                opcodeAndOperand = instructionJumpConditional(line, "GT");
            } else if (line.contains("JMPLT")) {
                opcodeAndOperand = instructionJumpConditional(line, "LT");
            } else if (line.contains("DATA")) {
                opcodeAndOperand = insturctionData(line);
            } else if (line.contains("JMP")) {
                opcodeAndOperand = instructionJump(line);
            } else {
                throw new IllegalArgumentException("Invalid instruction: " + line);
            }

            byteArray[j] = (byte) opcodeAndOperand[0];
            byteArray[j + 1] = (byte) opcodeAndOperand[1];
            j += 2;
        }
    }

    public byte[] instructionAdd(String line, String mode) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        switch (mode) {
            case "I":
                opcode = 0x50;
                break;
            case "F":
                opcode = 0x60;
                break;
            default:
                throw new IllegalArgumentException("Mode must be either F or I.");
        }

        line = line.replace("ADD", "");

        String[] splitLine = line.split(",");
        String register1 = splitLine[0].substring(2);
        String register2 = splitLine[1].split("->")[0].substring(1);
        String registerDestination = splitLine[1].split("->")[1].substring(1);

        opcode += (byte) (Integer.parseInt(registerDestination));
        operand = (byte) (Integer.parseInt(register1 + register2));

        return new byte[] { opcode, operand };
    }

    public byte[] instructionLogical(String line, String mode) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        switch (mode) {
            case "OR":
                opcode = (byte) 0x70;
                break;
            case "AND":
                opcode = (byte) 0x80;
                break;
            case "XOR":
                opcode = (byte) 0x90;
                break;
            default:
                throw new IllegalArgumentException("Mode must be either OR, AND, or XOR.");
        }

        line = line.replace(mode, "");
        String[] splitStrings = line.split(",");
        String register1 = splitStrings[0].substring(1);
        String register2 = splitStrings[1].split("->")[0].substring(1);
        String registerDestination = splitStrings[1].split("->")[1].substring(1);

        opcode += (byte) (Integer.parseInt(registerDestination, 16));
        operand = (byte) (Integer.parseInt((register1 + register2), 16));

        return new byte[] { opcode, operand };
    }

    public byte[] instructionRotate(String line) {
        byte opcode = (byte) 0xA0;
        byte operand = (byte) 0x00;
        line = line.replace("ROT", "");

        String[] splitStrings = line.split(",");
        String register = splitStrings[0].substring(1);
        System.out.println("yep" + register);

        String rotations = splitStrings[1];
        System.out.println("yep" + rotations);

        opcode += (byte) (Integer.parseInt("0" + register, 16));
        operand = (byte) (Integer.parseInt("0" + rotations, 16));

        return new byte[] { opcode, operand };
    }

    public byte[] insturctionData(String line) {
        line = line.replace("DATA", "");
        return new byte[] { (byte) Integer.parseInt(line), (byte) 0x00 };
    }

    public byte[] instructionJump(String line) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        if (line.contains("JMPR")) {
            opcode = (byte) 0xF0;
            line = line.replace("JMPR", "");
            operand = (byte) Integer.parseInt(line);
        } else {
            opcode = (byte) 0xB0;
            line = line.replace("JMP", "");
            operand = (byte) Integer.parseInt(line);
        }

        return new byte[] { opcode, operand };
    }

    public byte[] instructionJumpConditional(String line, String mode) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        switch (mode) {
            case "NE":
                opcode = (byte) 0x10;
                break;
            case "GE":
                opcode = (byte) 0x20;
                break;
            case "LE":
                opcode = (byte) 0x30;
                break;
            case "GT":
                opcode = (byte) 0x40;
                break;
            case "LT":
                operand = (byte) 0x50;
                break;
            default:
                System.out.println("Mode: " + mode);
                throw new IllegalArgumentException("Mode must be either NE, GE, LE, GT, or LT.");
        }

        line = line.replace("JMP" + mode + "R", "");
        String[] splitStrings = line.split(",R");
        String addRegister = splitStrings[0];
        String compRegister = splitStrings[1];
        opcode += (byte) (Integer.parseInt(addRegister, 16));
        operand = (byte) (Integer.parseInt(compRegister, 16));
        return new byte[] { opcode, operand };
    }

    public byte[] instructionMove(String line) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        line = line.replace("MOV", "");
        String[] arguments = line.split("->");

        String source = arguments[0];
        String destination = arguments[1];

        System.out.println("Source: " + source + " Destination: " + destination);

        if (source.startsWith("R")) {
            source = source.substring(1);
            if (destination.startsWith("R")) {
                opcode = (byte) 0x40;
                System.out.println("Source: " + source + " Destination: " + destination);
                operand = (byte) (Integer.parseInt(source.substring(1) + "0"));
                operand += (byte) (Integer.parseInt("0" + destination.substring(1)));
            } else if (destination.startsWith("[R")) {
                opcode = (byte) 0xE0;
                operand = (byte) (Integer.parseInt(source + "0"));
                operand += (byte) (Integer.parseInt("0" + destination.substring(2, 1)));
            } else {
                opcode = (byte) 0x30;
                operand = (byte) (Integer.parseInt(source + "0"));
                destination = destination.replace("[", "").replace("]", "");
                operand += (byte) (Integer.parseInt("0" + destination, 16));
            }

        } else if (source.startsWith("[R")) {
            opcode = (byte) 0xD0;
            operand += (byte) (Integer.parseInt("0" + source.substring(1)));
            operand = (byte) (Integer.parseInt(source.substring(2, 1), 16));
        } else if (source.startsWith("[")) {
            opcode = (byte) 0x10;
            operand = (byte) (Integer.parseInt("0" + source.substring(1, 2)));
            operand += (byte) (Integer.parseInt("0" + source.substring(1, 2), 16));
        } else {
            opcode = (byte) 0x20;
            operand = (byte) (Integer.parseInt("0" + destination.substring(1), 16));
            operand += (byte) (Integer.parseInt(source, 16));
        }
        return new byte[] { opcode, operand };
    }

    public byte[] instructionJumpEQ(String line) {
        byte opcode = (byte) 0x00;
        byte operand = (byte) 0x00;

        if (line.contains("JMPEQR")) {
            opcode = (byte) 0xF0;
            operand = (byte) 0x00;
            line = line.replace("JMPEQR", "");
            String[] splitStrings = line.split(",R");
            String addressRegister = splitStrings[0];
            String compRegister = splitStrings[1];
            opcode += (byte) (Integer.parseInt(addressRegister, 16));
            operand = (byte) (Integer.parseInt(compRegister, 16));
        } else {
            opcode = (byte) 0xB0;
            line = line.replace("JMPEQ", "");
            String[] splitStrings = line.split(",R");
            String addressRegister = splitStrings[0];
            String compRegister = splitStrings[1];
            opcode += (byte) (Integer.parseInt(addressRegister, 16));
            operand = (byte) (Integer.parseInt(compRegister, 16));
        }

        return new byte[] { opcode, operand };
    }

    public void printByteArray() {
        for (int i = 0; i < byteArray.length; i++) {
            System.out.println(byteArray[i]);
        }
    }

    public byte[] getByteArray() {
        return byteArray;
    }
}