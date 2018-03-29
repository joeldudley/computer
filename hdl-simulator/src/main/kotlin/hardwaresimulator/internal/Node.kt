package hardwaresimulator.internal

sealed class Node

open class ChipNode : Node()
object NandNode : ChipNode()
data class RegularChipNode(
        val name: String,
        val inputs: List<IOPinNode>,
        val outputs: List<IOPinNode>,
        val parts: List<PartNode>) : ChipNode()

data class IOPinNode(val name: String, val width: Int) : Node()
data class InternalPinNode(val name: String, val index: Int) : Node()
data class PartNode(val chip: ChipNode, val assignments: List<AssignmentNode>) : Node()
data class AssignmentNode(val lhs: InternalPinNode, val rhs: InternalPinNode) : Node()