package os.processes;

import os.processes.ProcessData;
import os.processes.ProcessSerializer;
import os.processes.ProcessState;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultProcessSerializer implements ProcessSerializer {

    @Override
    public String serialize(ProcessData process) {
        ArrayList<String> lines = new ArrayList<>();

        lines.add("[START PCB]");
        lines.add("pid=" + process.getProcessId());
        lines.add("pc=" + process.getProgramCounter());
        lines.add("size=" + process.getProcessSize());
        lines.add("start=" + process.getMemoryStart());
        lines.add("end=" + process.getMemoryEnd());
        lines.add("state=" + process.getProcessState());
        lines.add("[END PCB]");
        lines.add("");

        lines.add("[START INSTRUCTIONS]");
        lines.addAll(process.getInstructions());
        lines.add("[END INSTRUCTIONS]");
        lines.add("");

        lines.add("[START VARIABLES]");
        for (String name : process.getVariables().keySet()) {
            lines.add(name + "=" + process.getVariables().get(name));
        }
        lines.add("[END VARIABLES]");
        lines.add("");

        if (process.getReadFileTemp() != null) {
            lines.add("[READ FILE TEMP]");
            lines.add(process.getReadFileTemp());
            lines.add("[END READ FILE TEMP]");
        }


        return String.join("\n", lines);
    }

    @Override
    public ProcessData deserialize(String process) {
        int pid = 0;
        int pc = 0;
        int size = 0;
        int start = 0;
        int end = 0;
        ProcessState state = ProcessState.READY;
        ArrayList<String> instructions = new ArrayList<>();
        ArrayList<String> readFileTemp = new ArrayList<>();
        HashMap<String, Object> variables = new HashMap<>();

        String[] lines = process.split("\n");

        boolean inPCB = false;
        boolean inInstructions = false;
        boolean inVariables = false;
        boolean inReadFileTemp = false;

        for (String line : lines) {
            if (line.equals("")) {
                continue;
            }

            if (line.equals("[START PCB]")) {
                inPCB = true;
            } else if (line.equals("[END PCB]")) {
                inPCB = false;
            } else if (line.equals("[START INSTRUCTIONS]")) {
                inInstructions = true;
            } else if (line.equals("[END INSTRUCTIONS]")) {
                inInstructions = false;
            } else if (line.equals("[START VARIABLES]")) {
                inVariables = true;
            } else if (line.equals("[END VARIABLES]")) {
                inVariables = false;
            } else if (line.equals("[READ FILE TEMP]")) {
                inReadFileTemp = true;
            } else if (line.equals("[END READ FILE TEMP]")) {
                inReadFileTemp = false;
            } else if (inReadFileTemp) {
                readFileTemp.add(line);
            } else if (inPCB) {
                String[] parts = line.split("=");

                switch (parts[0]) {
                    case "pid":
                        pid = Integer.parseInt(parts[1]);
                        break;
                    case "pc":
                        pc = Integer.parseInt(parts[1]);
                        break;
                    case "size":
                        size = Integer.parseInt(parts[1]);
                        break;
                    case "start":
                        start = Integer.parseInt(parts[1]);
                        break;
                    case "end":
                        end = Integer.parseInt(parts[1]);
                        break;
                    case "state":
                        state = ProcessState.valueOf(parts[1]);
                        break;
                }
            } else if (inInstructions) {
                instructions.add(line);
            } else if (inVariables) {
                String[] parts = line.split("=");

                try {
                    variables.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    variables.put(parts[0], parts[1]);
                }
            }
        }

        return new ProcessData(
            pid,
            pc,
            size,
            start,
            end,
            state,
            instructions,
            variables,
            String.join("\n", readFileTemp)
        );
    }
}
