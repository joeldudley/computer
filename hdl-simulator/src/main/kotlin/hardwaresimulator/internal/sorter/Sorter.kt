package hardwaresimulator.internal.sorter

class ChipNameAndParts(val name: String, val dependencies: MutableSet<String>)
class SortNode(val name: String, val inEdges: MutableSet<SortNode>, val outEdges: MutableSet<SortNode>)

class Sorter {
    fun orderChipDefinitions(tokensList: List<List<String>>): MutableList<SortNode> {
        val chipNamesAndParts = tokensList.map { tokens -> extractChipNameAndParts(tokens) }

        val sortNodes = createDependencyGraph(chipNamesAndParts)

        val sortedElements = mutableListOf<SortNode>()
        val nodesWithNoEdges = sortNodes.filter { it.inEdges.isEmpty() }.toMutableSet()

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

        return sortedElements
    }

    fun createDependencyGraph(chipNamesAndParts: List<ChipNameAndParts>): List<SortNode> {
        val sortNodeMap = mutableMapOf<String, SortNode>()
        chipNamesAndParts.forEach { sortNodeMap.put(it.name, SortNode(it.name, mutableSetOf(), mutableSetOf())) }
        for (chipNameAndParts in chipNamesAndParts) {
            val sortNode = sortNodeMap[chipNameAndParts.name]!!
            for (dependency in chipNameAndParts.dependencies) {
                // TODO: Don't hardcode this.
                if (dependency != "Nand") {
                    sortNodeMap[dependency]!!.outEdges.add(sortNode)
                    sortNode.inEdges.add(sortNodeMap[dependency]!!)
                }
            }
        }
        return sortNodeMap.values.toList()
    }

    fun extractChipNameAndParts(tokens: List<String>): ChipNameAndParts {
        var pos = 1
        val name = tokens[pos]
        val dependencies = mutableSetOf<String>()

        while (tokens[pos] != "PARTS:") {
            pos++
        }
        pos++
        dependencies.add(tokens[pos])
        while (true) {
            while (tokens[pos] != ";") {
                pos++
            }
            pos++
            if (tokens[pos] == "}") {
                return ChipNameAndParts(name, dependencies)
            }
            dependencies.add(tokens[pos])
        }
    }
}