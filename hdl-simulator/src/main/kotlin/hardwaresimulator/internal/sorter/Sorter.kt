package hardwaresimulator.internal.sorter

data class ChipNameAndParts(val name: String, val parts: MutableSet<String>)
class ChipDependencyNode(val name: String, val inEdges: MutableSet<ChipDependencyNode>, val outEdges: MutableSet<ChipDependencyNode>)

class Sorter {
    fun orderChipDefinitions(tokensList: List<List<String>>): List<List<String>> {
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

    // TODO: Rename sortNodeMap references.
    private fun createDependencyGraph(chipNamesAndParts: List<ChipNameAndParts>): List<ChipDependencyNode> {
        val sortNodeMap = mutableMapOf<String, ChipDependencyNode>()
        chipNamesAndParts.forEach { sortNodeMap.put(it.name, ChipDependencyNode(it.name, mutableSetOf(), mutableSetOf())) }
        for ((name, parts) in chipNamesAndParts) {
            val sortNode = sortNodeMap[name]!!
            for (part in parts) {
                // TODO: Don't hardcode this.
                if (part != "Nand") {
                    sortNodeMap[part]!!.outEdges.add(sortNode)
                    sortNode.inEdges.add(sortNodeMap[part]!!)
                }
            }
        }
        return sortNodeMap.values.toList()
    }

    private fun topologicalSort(chipDependencyNodes: List<ChipDependencyNode>): List<String> {
        val sortedElements = mutableListOf<ChipDependencyNode>()
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