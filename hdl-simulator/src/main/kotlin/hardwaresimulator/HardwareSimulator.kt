package hardwaresimulator

interface HardwareSimulator {
    fun loadChipDefinitions(vararg chipDefinitionFolders: String)

    fun loadChip(name: String)

    fun setInputs(vararg inputValues: Pair<String, Boolean>)

    fun getValue(gateName: String): Boolean
}