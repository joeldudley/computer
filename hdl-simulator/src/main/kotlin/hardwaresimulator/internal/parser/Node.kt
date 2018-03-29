package hardwaresimulator.internal.parser

sealed class Node

data class ChipNode(val name: String, val inputs: List<IOPinNode>, val outputs: List<IOPinNode>, val parts: List<PartNode>) : Node()
data class IOPinNode(val name: String, val width: Int) : Node()
data class InternalPinNode(val name: String, val index: Int) : Node()
data class PartNode(val name: String, val assignments: List<AssignmentNode>) : Node()
data class AssignmentNode(val lhs: InternalPinNode, val rhs: InternalPinNode) : Node()