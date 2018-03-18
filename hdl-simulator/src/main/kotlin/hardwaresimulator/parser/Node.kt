package hardwaresimulator.parser

sealed class Node {
    data class Chip(
            val name: String,
            val ins: List<Pin>,
            val outs: List<Pin>,
            val components: List<Component>)

    data class Pin(
            val name: String,
            val width: Int
    )

    data class Component(
           val name: String,
           val assignments: List<Assignment>
    )

    data class Assignment(
            val lhs: String,
            val rhs: String
    )
}