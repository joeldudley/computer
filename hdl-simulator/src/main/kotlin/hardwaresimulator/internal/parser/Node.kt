package hardwaresimulator.internal.parser

sealed class Node {
    data class Chip(
            val name: String,
            val ins: List<IOPin>,
            val outs: List<IOPin>,
            val parts: List<Part>)

    data class IOPin(
            val name: String,
            val width: Int
    )

    data class InternalPin(
            val name: String,
            val index: Int
    )

    data class Part(
           val name: String,
           val assignments: List<Assignment>
    )

    data class Assignment(
            val lhs: InternalPin,
            val rhs: InternalPin
    )
}