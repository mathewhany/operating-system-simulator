package os;

import os.processes.Process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class SystemCalls {
    public static void semWait(Kernel kernel, Process process, String mutexName) {
        Mutex mutex = kernel.getMutex(mutexName);
        mutex.semWait(process.getPcb().getProcessId());
    }

    public static void semSignal(Kernel kernel, Process process, String mutexName) {
        Mutex mutex = kernel.getMutex(mutexName);
        mutex.semSignal(process.getPcb().getProcessId());
    }

    public static void assign(Process process, String variableName, String sourceVariable) {
        process.setVariable(variableName, process.getVariable(sourceVariable));
    }

    public static void print(Process process, String variableName) {
        System.out.println(process.getVariable(variableName));
    }

    public static void writeFile(Process process, String filePath, String contentVariable) throws OSException {
        try {
            Files.writeString(Paths.get(filePath), (String) process.getVariable(contentVariable));
        } catch (IOException e) {
            throw new OSException("File not found " + filePath);
        }
    }

    public static void readFile(Process process, String filePath) throws OSException {
        try {
            String content = Files.readString(Paths.get(filePath));
            process.setTemp(content);
        } catch (IOException e) {
            throw new OSException("File not found " + filePath);
        }
    }

    public static void printFromTo(Process process, String from, String to) {
        int x = Integer.parseInt((String) process.getVariable(from));
        int y = Integer.parseInt((String) process.getVariable(to));

        int i = Math.min(x, y);
        int j = Math.max(x, y);

        for (; i <= j; i++) {
            System.out.println(i);
        }
    }

    public static void input(Process process) {
        System.out.print("Please enter a value: ");
        Scanner sc = new Scanner(System.in);
        String input = sc.next();
        process.setTemp(input);
    }
}
