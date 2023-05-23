package os;

import os.memory.MemoryAllocator;
import os.memory.MemoryBlock;
import os.processes.PCB;
import os.processes.Process;
import os.processes.ProcessState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ProcessManager {
    private final HashMap<Integer, Process> processes = new HashMap<>();
    private final String processStoragePath;
    private final Kernel kernel;

    private int nextProcessId = 0;

    public ProcessManager(String processStoragePath, Kernel kernel) {
        this.processStoragePath = processStoragePath;
        this.kernel = kernel;
    }

    public Process createProcess(String path) {
        try {
            List<String> instructions = Files.readAllLines(Paths.get(path));
            int processSize = calculateProcessSize(instructions);

            Process process = new Process(
                nextProcessId++,
                instructions,
                allocateMemoryForProcess(processSize)
            );

            processes.put(process.getPcb().getProcessId(), process);

            return process;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int calculateProcessSize(List<String> instructions) {
        int pcbSize = 5;
        int instructionsSize = instructions.size();
        int variablesSize = 3;

        return pcbSize + instructionsSize + variablesSize;
    }

    public Process getProcess(int processId) {
        if (!processes.containsKey(processId)) {
            return loadProcessFromDisk(processId);
        }

        return processes.get(processId);
    }

    public void removeProcess(int processId) {
        MemoryAllocator allocator = kernel.getMemoryAllocator();
        PCB pcb = processes.get(processId).getPcb();
        allocator.free(pcb.getMemoryStart(), pcb.getProcessSize());
        processes.remove(processId);
    }

    private void saveProcessToDisk(int processId) {
        Process process = getProcess(processId);
    }

    private Process loadProcessFromDisk(int processId) {
//        try {
//            List<String> memoryDump = Files.readAllLines(getPathForMemoryDump(processId));
//            int processSize = memoryDump.size();
//            Process process = new Process(memoryDump, allocateMemoryForProcess(processSize));
//            processes.put(process.getProcessId(), process);
//            return process;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    private MemoryBlock allocateMemoryForProcess(int size) {
        int start = kernel.getMemoryAllocator().allocate(size);

        if (start == -1) {
            Process processToSwapWith = getFirstBlockedProcess();
            if (processToSwapWith == null) {
                processToSwapWith = processes.get(0);
            }
            saveProcessToDisk(processToSwapWith.getPcb().getProcessId());
            return allocateMemoryForProcess(size);
        }

        return new MemoryBlock(start, size, kernel.getMemory());
    }

    private Process getFirstBlockedProcess() {
        for (int processId : processes.keySet()) {
            Process process = processes.get(processId);
            if (process.getPcb().getProcessState() == ProcessState.BLOCKED) {
                return process;
            }
        }

        return null;
    }

    private Path getPathForMemoryDump(int processId) {
        String directory = processStoragePath + File.separator + processId;

        try {
            Files.createDirectories(Path.of(directory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Path.of(directory, "memoryDump.txt");
    }
}
