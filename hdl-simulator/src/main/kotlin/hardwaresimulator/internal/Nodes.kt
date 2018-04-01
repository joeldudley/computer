package hardwaresimulator.internal

sealed class Node

data class ChipNode(val name: String, val inputs: List<IONode>, val outputs: List<IONode>, val parts: List<PartNode>) : Node()
data class IONode(val name: String, val width: Int) : Node()
data class PartNode(val chip: ChipNode, val inputAssignments: List<AssignmentNode>, val outputAssignments: List<AssignmentNode>) : Node()
data class AssignmentNode(val lhs: LHSNode, val rhs: RHSNode) : Node()
data class LHSNode(val name: String) : Node()
data class RHSNode(val name: String, val indexed: Boolean, val startIndex: Int?, val endIndex: Int?) : Node()