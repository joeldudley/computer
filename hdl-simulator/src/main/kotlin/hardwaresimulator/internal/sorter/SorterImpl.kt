package hardwaresimulator.internal.sorter

class SorterImpl: Sorter {
    private class ChipNameAndParts(val name: String, val parts: MutableSet<String>)
    private class DependencyNode(val name: String, val inEdges: MutableSet<DependencyNode>, val outEdges: MutableSet<DependencyNode>)

    override fun topologicallySortChipDefinitions(tokensList: List<List<String>>): List<List<String>> {
        val chipNamesAndParts = tokensList.map { tokens -> extractChipNameAndParts(tokens) }
        val chipNames = chipNamesAndParts.map { it.name }
        val chipNameToTokensMap = chipNames.zip(tokensList).toMap()

        val chipDependencyNodes = createDependencyGraph(chipNamesAndParts)
        val sortedChipNames = topologicalSort(chipDependencyNodes)

        return sortedChipNames.map { sortedChipName -> chipNameToTokensMap[sortedChipName]!! }
    }

    private fun extractChipNameAndParts(tokens: List<String>): ChipNameAndParts {
        var pos = 1
        val name = tokens[pos]
        val chip = ChipNameAndParts(name, mutableSetOf())

        while (tokens[pos] != "PARTS:") {
            pos++
        }
        pos++
        chip.parts.add(tokens[pos])
        while (true) {
            while (tokens[pos] != ";") {
                pos++
            }
            pos++
            if (tokens[pos] == "}") {
                return chip
            }
            chip.parts.add(tokens[pos])
        }
    }

    private fun createDependencyGraph(chipNamesAndParts: List<ChipNameAndParts>): List<DependencyNode> {
        val dependencyNodeMap = chipNamesAndParts.map { it.name to DependencyNode(it.name, mutableSetOf(), mutableSetOf()) }.toMap()
        for (chipNameAndPart in chipNamesAndParts) {
            val dependencyNode = dependencyNodeMap[chipNameAndPart.name]!!
            for (part in chipNameAndPart.parts) {
                // TODO: Don't hardcode this.
                if (part != "Nand") {
                    val partNode = dependencyNodeMap[part]!!
                    partNode.outEdges.add(dependencyNode)
                    dependencyNode.inEdges.add(partNode)
                }
            }
        }
        return dependencyNodeMap.values.toList()
    }

    private fun topologicalSort(chipDependencyNodes: List<DependencyNode>): List<String> {
        val sortedElements = mutableListOf<DependencyNode>()
        val nodesWithNoEdges = chipDependencyNodes.filter { it.inEdges.isEmpty() }.toMutableSet()

        while (nodesWithNoEdges.isNotEmpty()) {
            val n = nodesWithNoEdges.last()
            nodesWithNoEdges.remove(n)
            sortedElements.add(n)

            for (node in n.outEdges) {
                node.inEdges.remove(n)
                if (node.inEdges.isEmpty()) {
                    nodesWithNoEdges.add(node)
                }
            }
        }

        return sortedElements.map { it.name }
    }
}