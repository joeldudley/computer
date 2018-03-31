package hardwaresimulator

interface HardwareSimulator {
    fun loadLibraryChips(vararg paths: String)

    fun loadChip(path: String)

    fun setInputs(vararg inputValues: Pair<String, Boolean>)

    fun getOutput(gateName: String): Boolean
}