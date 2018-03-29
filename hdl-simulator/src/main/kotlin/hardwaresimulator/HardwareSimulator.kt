package hardwaresimulator

interface HardwareSimulator {
    fun loadChipDefinitions(vararg chipDefinitionFolders: String)

    fun loadChip(name: String)

    fun setInputs(vararg inputs: Pair<String, Boolean>)

    fun getValue(gateName: String): Boolean
}