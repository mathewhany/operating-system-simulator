package os;

import os.memory.MemoryBlock;
import os.memory.Variable;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private final int MEMORY_START = 0;
    private final int SIZE = 1;
    private final int PROCESS_ID = 2;
    private final int PROCESS_STATE = 3;
    private final int PROGRAM_COUNTER = 4;
    private final int FIRST_INSTRUCTION = 5;

    private final MemoryBlock memory;

    public Process(int id, List<String> instructions, MemoryBlock memory) {
        this.memory = memory;

        setProcessId(id);
        setProcessState(ProcessState.READY);
        setMemoryStart(memory.getStart());
        setSize(memory.getSize());
        setProgramCounter(0);
        setInstructions(instructions);
    }

    public int getMemoryStart() {
        return (int) memory.read(MEMORY_START);
    }

    private void setMemoryStart(int memoryStart) {
        memory.write(MEMORY_START, memoryStart);
    }

    public int getProcessId() {
        return (int) memory.read(PROCESS_ID);
    }

    private void setProcessId(int id) {
        memory.write(PROCESS_ID, id);
    }

    public ProcessState getProcessState() {
        return (ProcessState) memory.read(PROCESS_STATE);
    }

    public void setProcessState(ProcessState processState) {
        memory.write(PROCESS_STATE, processState);
    }

    public int getSize() {
        return (int) memory.read(SIZE);
    }

    private void setSize(int size) {
        memory.write(SIZE, size);
    }

    public int getProgramCounter() {
        return (int) memory.read(PROGRAM_COUNTER);
    }

    private void setProgramCounter(int programCounter) {
        memory.write(PROGRAM_COUNTER, programCounter);
    }

    public String getNextInstruction() {
        int programCounter = getProgramCounter();

        return (String) memory.read(FIRST_INSTRUCTION + programCounter);
    }

    public void incrementProgramCounter() {
        int programCounter = getProgramCounter();

        setProgramCounter(programCounter + 1);
    }

    public int getInstructionCount() {
        return getSize() - Constants.MAX_VARIABLES - FIRST_INSTRUCTION;
    }

    public boolean hasInstructions() {
        int programCounter = getProgramCounter();

        return programCounter < getInstructionCount();
    }

    private void setInstructions(List<String> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            memory.write(FIRST_INSTRUCTION + i, instructions.get(i));
        }
    }

    public Object getVariable(String name) {
        for (int i = 0; i < Constants.MAX_VARIABLES; i++) {
            Variable value = (Variable) memory.read(getSize() - 1 - i);

            if (value != null && value.getName().equals(name)) {
                return memory.read(i + 1);
            }
        }

        throw new RuntimeException("Variable " + name + " not found");
    }

    public void setVariable(String name, Object value) {
        for (int i = 0; i < Constants.MAX_VARIABLES; i++) {
            int address = getSize() - 1 - i;
            Variable variable = (Variable) memory.read(address);

            if (variable != null && variable.getName().equals(name)) {
                memory.write(address, value);
                return;
            }
        }

        for (int i = 0; i < Constants.MAX_VARIABLES; i++) {
            int address = getSize() - 1 - i;
            Variable variable = (Variable) memory.read(address);

            if (variable == null) {
                memory.write(address, new Variable(name, value));
                return;
            }
        }
    }

    public String memoryDump() {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            Object value = memory.read(i);
            String line;

            if (value instanceof Variable) {
                Variable variable = (Variable) value;
                line = variable.getName() + "=" + variable.getValue();
            } else {
                line = value == null ? "null" : value.toString();
            }

            lines.add(line);
        }

        return String.join("\n", lines);
    }

    public Process(List<String> memoryDump, MemoryBlock memory) {
        this.memory = memory;

        int processId = Integer.parseInt(memoryDump.get(PROCESS_ID));
        ProcessState processState = ProcessState.valueOf(memoryDump.get(PROCESS_STATE));
        int programCounter = Integer.parseInt(memoryDump.get(PROGRAM_COUNTER));
        int size = memory.getSize();

        memory.write(MEMORY_START, memory.getStart());
        memory.write(SIZE, size);
        memory.write(PROCESS_ID, processId);
        memory.write(PROCESS_STATE, processState);
        memory.write(PROGRAM_COUNTER, programCounter);

        int instructionCount = memoryDump.size() - Constants.MAX_VARIABLES - FIRST_INSTRUCTION;

        for (int i = 0; i < instructionCount; i++) {
            int address = FIRST_INSTRUCTION + i;
            if (memoryDump.get(address) != null) {
                memory.write(address, memoryDump.get(address));
            }
        }

        for (int i = 0; i < Constants.MAX_VARIABLES; i++) {
            int address = size - 1 - i;
            if (memoryDump.get(address) != null) {
                String variableDump = memoryDump.get(address);
                String[] variableDumpParts = variableDump.split("=");
                String name = variableDumpParts[0];
                String value = variableDumpParts[1];

                memory.write(address, new Variable(name, value));
            }
        }
    }
}
