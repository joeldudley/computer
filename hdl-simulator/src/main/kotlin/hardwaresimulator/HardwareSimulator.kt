package hardwaresimulator

interface HardwareSimulator {
    fun loadChipDefinitions(vararg paths: String)

    fun loadChip(name: String)

    fun setInputs(vararg inputValues: Pair<String, Boolean>)

    fun getOutput(gateName: String): Boolean
}