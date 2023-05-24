package os;

import os.processes.Process;

public class InstructionExecutor {
    private final Kernel kernel;

    public InstructionExecutor(Kernel kernel) {
        this.kernel = kernel;
    }

    public void execute(Process process, String instruction) throws OSException {
        String[] line = instruction.split(" ");

        if (line[0].equals("semWait") && line.length == 2) {
            SystemCalls.semWait(kernel, process, line[1]);
        } else if (line[0].equals("semSignal") && line.length == 2) {
            SystemCalls.semSignal(kernel, process, line[1]);
        } else if (line[0].equals("assign") && line.length == 3 && line[2].matches("input")) {
            if (process.getTemp() == null) {
                SystemCalls.input(process);
                return;
            } else {
                SystemCalls.assign(process, line[1], process.getTemp());
            }
        } else if (line[0].equals("assign") && line.length == 4 && line[2].equals("readFile")) {
            if (process.getTemp() == null) {
                SystemCalls.readFile(process, line[3]);
                return;
            } else {
                SystemCalls.assign(process, line[1], process.getTemp());
            }
        } else if (line[0].equals("assign") && line.length == 3) {
            SystemCalls.assign(process, line[1], line[2]);
        } else if (line[0].equals("print") && line.length == 2) {
            SystemCalls.print(process, line[1]);
        } else if (line[0].equals("writeFile") && line.length == 3) {
            SystemCalls.writeFile(process, line[1], line[2]);
        } else if (line[0].equals("printFromTo") && line.length == 3) {
            SystemCalls.printFromTo(process, line[1], line[2]);
        } else {
            throw new OSException("Invalid instruction " + instruction);
        }

        process.incrementProgramCounter();
    }

}
