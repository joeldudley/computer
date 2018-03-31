package hardwaresimulator.internal.sorter

interface Sorter {
    fun topologicallySortChipDefinitions(tokensList: List<List<String>>): List<List<String>>
}