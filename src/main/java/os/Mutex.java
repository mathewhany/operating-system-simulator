package os;

import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
    private final Kernel kernel;
    private int owner = -1;
    private final Queue<Integer> blockedQueue = new LinkedList<>();

    public Mutex(Kernel kernel) {
        this.kernel = kernel;
    }

    public void semWait(int processId) {
        if (owner == -1) {
            owner = processId;
        } else {
            blockedQueue.add(processId);
            kernel.getScheduler().blockProcess(processId);
        }
    }

    public void semSignal(int processId) {
        if (owner != processId) {
            return;
        }

        if (blockedQueue.isEmpty()) {
            owner = -1;
        } else {
            int nextProcessId = blockedQueue.remove();
            kernel.getScheduler().unblockProcess(nextProcessId);
            owner = nextProcessId;
        }
    }
}
