package hardwaresimulator;

import java.util.List;

interface HardwareSimulator {
    void loadChipDefinitions(String path);
    void loadChipDefinitions(List<String> path);

    void loadChip(String name);

    void setInput(String name, List<Boolean> values);

    List<Boolean> getOutput(String gateName);
}