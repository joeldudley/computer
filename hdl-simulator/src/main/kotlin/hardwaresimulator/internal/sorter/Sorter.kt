package hardwaresimulator.internal.sorter

data class ChipPartsAndTokens(val dependencies: MutableSet<String>, val tokens: List<String>)
class SortNode(val name: String, val inEdges: MutableSet<SortNode>, val outEdges: MutableSet<SortNode>)

class Sorter {
    fun orderChipDefinitions(tokensList: List<List<String>>): List<List<String>> {
        // TODO: Split these steps into extracting the name and extracting the dependencies.
        val chipMap = tokensList.map { tokens -> extractChipNameAndParts(tokens)}.toMap()

        val sortNodes = createDependencyGraph(chipMap)

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

        return sortedElements.map { sortNode -> chipMap[sortNode.name]!!.tokens }
    }

    fun createDependencyGraph(chipNamesAndParts: Map<String, ChipPartsAndTokens>): List<SortNode> {
        val sortNodeMap = mutableMapOf<String, SortNode>()
        chipNamesAndParts.keys.forEach { sortNodeMap.put(it, SortNode(it, mutableSetOf(), mutableSetOf())) }
        for ((name, chipPartsAndTokens) in chipNamesAndParts) {
            val sortNode = sortNodeMap[name]!!
            for (dependency in chipPartsAndTokens.dependencies) {
                // TODO: Don't hardcode this.
                if (dependency != "Nand") {
                    sortNodeMap[dependency]!!.outEdges.add(sortNode)
                    sortNode.inEdges.add(sortNodeMap[dependency]!!)
                }
            }
        }
        return sortNodeMap.values.toList()
    }

    fun extractChipNameAndParts(tokens: List<String>): Pair<String, ChipPartsAndTokens> {
        var pos = 1
        val name = tokens[pos]
        val chip = ChipPartsAndTokens(mutableSetOf(), tokens)

        while (tokens[pos] != "PARTS:") {
            pos++
        }
        pos++
        chip.dependencies.add(tokens[pos])
        while (true) {
            while (tokens[pos] != ";") {
                pos++
            }
            pos++
            if (tokens[pos] == "}") {
                return name to chip
            }
            chip.dependencies.add(tokens[pos])
        }
    }
}