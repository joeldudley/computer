package hardwaresimulator

interface HardwareSimulator {
    fun loadChipDefinitions(vararg paths: String)

    fun loadChip(name: String)

    fun setInputs(vararg inputValues: Pair<String, List<Boolean>>)

    fun getOutput(gateName: String): List<Boolean>
}