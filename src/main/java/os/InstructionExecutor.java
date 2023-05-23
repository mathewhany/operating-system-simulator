package os;

import os.processes.Process;
import os.processes.ProcessManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class InstructionExecutor {
    private final Kernel kernel;

    public InstructionExecutor(Kernel kernel, ProcessManager processManager) {
        this.kernel = kernel;
    }

    public void execute(Process process, String instruction)  throws Exception {
        String[] line = instruction.split(" ");
        if (line[0].equals("semWait")) {
            if (line.length != 2)
                throw new Exception("the instruction is unvalid");
            kernel.getMutex(line[1]).semWait(process.getPcb().getProcessId());
            process.incrementProgramCounter();
        }
        else if (line[0].equals("semSignal")) {
            if (line.length !=2)
                throw  new Exception("the instruction is unvalid");
            kernel.getMutex(line[1]).semSignal(process.getPcb().getProcessId());
            process.incrementProgramCounter();
        }
        else if (line[0].equals("assign")) {
            if (line.length != 3   || line.length !=4)
                throw new Exception (" the instruction is unvalid ");
            Scanner sc = new Scanner(System.in);
            if (line[line.length-1].equals("input"))
                line[2] = sc.next();
            else if (line[2].equals("readFile")) {
                if (process.getReadFileTemp() == null) {
                    if(line.length !=4 )
                        throw new Exception("the instruction is unvalid")
                    String filePath = (String) process.getVariable(line[3]);
                    try

                    {
                        String content = Files.readString(Paths.get(filePath));
                        process.setReadFileTemp(content);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    return ;
                }
                else
                    line[2] = process.getReadFileTemp();
                    process.setReadFileTemp(null);
            }
           String var = (String) process.getVariable(line[1]);
           process.setVariable(var,line[2]);
           process.incrementProgramCounter();
        }
        else if (line[0].equals("print")){
            if (line.length !=2 )
                throw new Exception("the instruction is unvalid ");
            System.out.println(process.getVariable(line[1]));
            process.incrementProgramCounter();
        }
        else if (line[0].equals("writeFile")) {
            if (line.length != 3)
                throw new Exception("the instruction is unvalid");
            String content = (String) process.getVariable(line[2]);
            try {
                String filePath = (String) process.getVariable(line[1]);
                Files.writeString(Paths.get(filePath), content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.incrementProgramCounter();
        }
        else if (line[0].equals("printFromTo")) {
            if (line.length != 3 )
                throw new Exception("the instruction is unvalid");
            int x = Integer.parseInt(line[1]);
            int y = Integer.parseInt(line[2]);
            int i = Math.min(x,y);
            while (i < Math.max(x,y)) {
                System.out.println(i);
                i ++ ;
            }
                process.incrementProgramCounter();
        }
        }

}
