package hardwaresimulator.internal

sealed class Node

data class ChipNode(val name: String, val inputs: List<IONode>, val outputs: List<IONode>, val parts: List<PartNode>) : Node()
data class IONode(val name: String, val width: Int) : Node()
data class PartNode(val chip: ChipNode, val assignments: List<AssignmentNode>) : Node()
// TODO: Crap workaround to allow subclasses to be data classes, allowing test comparisons to work.
abstract class AssignmentNode: Node() {
    abstract val lhs: LHSNode
    abstract val rhs: RHSNode
}
data class InputAssignmentNode(override val lhs: LHSNode, override val rhs: RHSNode) : AssignmentNode()
data class OutputAssignmentNode(override val lhs: LHSNode, override val rhs: RHSNode) : AssignmentNode()
data class LHSNode(val name: String) : Node()
data class RHSNode(val name: String, val indexed: Boolean, val startIndex: Int?, val endIndex: Int?) : Node()