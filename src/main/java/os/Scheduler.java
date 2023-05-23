package os;

import os.processes.Process;
import os.processes.ProcessManager;
import os.processes.ProcessState;

import java.util.*;

public class Scheduler {
    private final Queue<Integer> readyQueue = new LinkedList<>();
    private final Queue<Integer> blockedProcesses = new LinkedList<>();

    private final int quantum;
    private final Kernel kernel;
    private int clock;
    private final HashMap<Integer, List<String>> scheduledPrograms = new HashMap<>();

    public Scheduler(int quantum, Kernel kernel) {
        this.quantum = quantum;
        this.kernel = kernel;
    }


    public void scheduleProgramAt(String path, int clockCycle) {
        if (scheduledPrograms.containsKey(clockCycle)) {
            scheduledPrograms.get(clockCycle).add(path);
        } else {
            List<String> programs = new ArrayList<>();
            programs.add(path);
            scheduledPrograms.put(clockCycle, programs);
        }
    }

    public void addScheduledPrograms() {
        ProcessManager processManager = kernel.getProcessManager();

        if (scheduledPrograms.containsKey(clock)) {
            for (String path : scheduledPrograms.get(clock)) {
                Process process = processManager.createProcess(path);
                readyQueue.add(process.getPcb().getProcessId());
            }
            scheduledPrograms.remove(clock);
        }
    }

    public void run() {
        ProcessManager processManager = kernel.getProcessManager();
        InstructionExecutor instructionExecutor = kernel.getInstructionExecutor();

        addScheduledPrograms();

        while (!readyQueue.isEmpty() || !scheduledPrograms.isEmpty()) {
            if (!readyQueue.isEmpty()) {
                int processId = readyQueue.remove();
                Process process = processManager.getProcess(processId);

                boolean hasMoreInstructions = process.hasInstructions();
                for (int i = 0; i < quantum && process.hasInstructions() &&
                                process.getPcb().getProcessState() != ProcessState.BLOCKED; i++) {
                    String instruction = process.getNextInstruction();
                    instructionExecutor.execute(process, instruction);
                    clock++;
                    hasMoreInstructions = process.hasInstructions();
                    addScheduledPrograms();
                }

                if (process.getPcb().getProcessState() != ProcessState.BLOCKED) {
                    if (hasMoreInstructions) {
                        readyQueue.add(processId);
                    } else {
                        processManager.removeProcess(processId);
                    }
                }
            } else {
                clock++;
                addScheduledPrograms();
            }
        }
    }

    public void addProcess(int processId) {
        readyQueue.add(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.READY);
    }

    public void blockProcess(int processId) {
        blockedProcesses.add(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.BLOCKED);
    }

    public void unblockProcess(int processId) {
        blockedProcesses.remove(processId);
        kernel.getProcessManager()
              .getProcess(processId)
              .getPcb()
              .setProcessState(ProcessState.READY);
    }

    public List<Integer> getBlockedProcesses() {
        return new ArrayList<>(blockedProcesses);
    }
}
