package os.processes;

import os.memory.MemoryBlock;

public class PCB extends MemoryBlock {
    public static final int PCB_SIZE = 5;

    private final int POS_MEMORY_START = 0;
    private final int POS_PROCESS_SIZE = 1;
    private final int POS_PROCESS_ID = 2;
    private final int POS_PROCESS_STATE = 3;
    private final int POS_PC = 4;

    public PCB(int pid, MemoryBlock logicalMemory) {
        super(0, PCB_SIZE, logicalMemory);

        setMemoryStart(logicalMemory.getStart());
        setProcessSize(logicalMemory.getSize());
        setProcessId(pid);
        setProcessState(ProcessState.READY);
        setProgramCounter(0);
    }

    public int getMemoryStart() {
        return (int) read(POS_MEMORY_START);
    }

    private void setMemoryStart(int memoryStart) {
        write(POS_MEMORY_START, memoryStart);
    }

    public int getProcessId() {
        return (int) read(POS_PROCESS_ID);
    }

    private void setProcessId(int id) {
        write(POS_PROCESS_ID, id);
    }

    public ProcessState getProcessState() {
        return (ProcessState) read(POS_PROCESS_STATE);
    }

    public void setProcessState(ProcessState processState) {
        write(POS_PROCESS_STATE, processState);
    }

    public int getProcessSize() {
        return (int) read(POS_PROCESS_SIZE);
    }

    private void setProcessSize(int size) {
        write(POS_PROCESS_SIZE, size);
    }

    public int getProgramCounter() {
        return (int) read(POS_PC);
    }

    private void setProgramCounter(int programCounter) {
        write(POS_PC, programCounter);
    }

    public void incrementProgramCounter() {
        setProgramCounter(getProgramCounter() + 1);
    }
}
