package hardwaresimulator.parser

sealed class Node {
    data class Chip(
            val name: String,
            val ins: List<IOPin>,
            val outs: List<IOPin>,
            val components: List<Component>)

    data class IOPin(
            val name: String,
            val width: Int
    )

    data class Pin(
            val name: String,
            val index: Int
    )

    data class Component(
           val name: String,
           val assignments: List<Assignment>
    )

    data class Assignment(
            val lhs: Pin,
            val rhs: Pin
    )
}