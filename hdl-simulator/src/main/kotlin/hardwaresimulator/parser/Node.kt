package hardwaresimulator.parser

sealed class Node {
    data class Chip(
            val name: String,
            val ins: List<String>,
            val outs: List<String>,
            val components: List<Component>)

    data class Component(
           val name: String,
           val assignments: List<Assignment>
    )

    data class Assignment(
            val lhs: String,
            val rhs: String
    )
}