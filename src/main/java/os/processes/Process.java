package os.processes;

import os.memory.MemoryBlock;
import os.memory.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Process {
    public static final int MAX_VARIABLES = 3;

    private final PCB pcb;
    private final MemoryBlock instructions;
    private final MemoryBlock variables;
    private String readFileTemp;


    public Process(int id, List<String> instructions, MemoryBlock memory) {
        this.pcb = new PCB(id, memory);
        this.instructions = new MemoryBlock(pcb.getEnd(), instructions.size(), memory);
        this.variables =
            new MemoryBlock(this.instructions.getEnd(), MAX_VARIABLES, memory);

        setInstructions(instructions);
    }

    public PCB getPcb() {
        return pcb;
    }

    public String getNextInstruction() {
        int programCounter = pcb.getProgramCounter();

        return (String) instructions.read(programCounter);
    }

    public List<String> getInstructions() {
        ArrayList<String> instructions = new ArrayList<>();

        for (int i = 0; i < getInstructionCount(); i++) {
            instructions.add((String) this.instructions.read(i));
        }

        return instructions;
    }

    public void incrementProgramCounter() {
        pcb.incrementProgramCounter();
    }

    public int getInstructionCount() {
        return instructions.getSize();
    }

    public boolean hasInstructions() {
        int pc = pcb.getProgramCounter();

        return pc < getInstructionCount();
    }

    private void setInstructions(List<String> instructions) {
        for (int i = 0; i < instructions.size(); i++) {
            this.instructions.write(i, instructions.get(i));
        }
    }

    public Object getVariable(String name) {
        HashMap<String, Variable> existingVariables = getVariables();

        if (existingVariables.containsKey(name)) {
            return existingVariables.get(name).getValue();
        }

        throw new RuntimeException("Variable " + name + " not found");
    }

    public void setVariable(String name, Object value) {
        HashMap<String, Variable> existingVariables = getVariables();

        if (existingVariables.containsKey(name)) {
            existingVariables.get(name).setValue(value);
            return;
        }

        for (int i = 0; i < variables.getSize(); i++) {
            Variable variable = (Variable) variables.read(i);

            if (variable == null) {
                variables.write(i, new Variable(name, value));
                return;
            }
        }
    }

    public HashMap<String, Variable> getVariables() {
        HashMap<String, Variable> variables = new HashMap<>();

        for (int i = 0; i < this.variables.getSize(); i++) {
            Variable variable = (Variable) this.variables.read(i);

            if (variable != null) {
                variables.put(variable.getName(), variable);
            }
        }

        return variables;
    }

    public Process(ProcessData data, MemoryBlock memory) {
        this(data.getProcessId(), data.getInstructions(), memory);
    }

    public ProcessData toProcessData() {
        HashMap<String, Object> variables = new HashMap<>();

        for (Variable variable : getVariables().values()) {
            variables.put(variable.getName(), variable.getValue());
        }

        return new ProcessData(
            pcb.getProcessId(),
            pcb.getProgramCounter(),
            pcb.getProcessSize(),
            pcb.getMemoryStart(),
            pcb.getEnd(),
            pcb.getProcessState(),
            getInstructions(),
            variables,
            readFileTemp
        );
    }

    public static int calculateProcessSize(List<String> instructions) {
        return instructions.size() + MAX_VARIABLES + PCB.PCB_SIZE;
    }

    public String getReadFileTemp() {
        return readFileTemp;
    }

    public void setReadFileTemp(String contents) {
        this.readFileTemp = contents;
    }
}
