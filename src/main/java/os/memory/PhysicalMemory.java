package os.memory;

public class PhysicalMemory implements Memory {
    private final Object[] memory;
    private final boolean[] allocated;

    public PhysicalMemory(int size) {
        this.memory = new Object[size];
        this.allocated = new boolean[size];
    }

    public Object read(int address) {
        return memory[address];
    }

    public void write(int address, Object data) {
        memory[address] = data;
    }

    public int size() {
        return memory.length;
    }

    public int allocate(int size) {
        int start = -1;
        int count = 0;
        for (int i = 0; i < memory.length; i++) {
            if (allocated[i]) {
                start = -1;
                count = 0;
            } else {
                if (start == -1) {
                    start = i;
                }
                count++;
                if (count == size) {
                    for (int j = start; j < start + size; j++) {
                        allocated[j] = true;
                    }
                    return start;
                }
            }
        }
        return -1;
    }

    public void free(int address, int size) {
        for (int i = address; i < address + size; i++) {
            allocated[i] = false;
            memory[i] = null;
        }
    }
}
