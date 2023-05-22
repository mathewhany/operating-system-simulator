package os.memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryBlock implements Memory {
    private final int start;
    private final int size;
    private final Memory memory;

    public MemoryBlock(int start, int size, Memory memory) {
        this.start = start;
        this.size = size;
        this.memory = memory;
    }

    public Object read(int address) {
        return memory.read(start + address);
    }

    public void write(int address, Object value) {
        memory.write(start + address, value);
    }

    public int getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }
}
