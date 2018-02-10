package hdlsimulator

import hdlsimulator.parser.Node

val EXPECTED_NOT_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "in", "b", "=", "in", "out", "=", "out", ")", ";", "}")
val EXPECTED_AND_TOKENS = listOf("CHIP", "And", "{", "IN", "a", "b", ";", "OUT", "out", ";", "PARTS:", "Nand", "(",
        "a", "=", "a", "b", "=", "b", "out", "=", "nandout", ")", ";", "Not", "(", "in", "=", "nandout",
        "out", "=", "out", ")", ";", "}")

val EXPECTED_NOT_TREE = {
    val ins = listOf("in")
    val outs = listOf("out")
    val components = listOf(
            Node.Component(
                    "Nand",
                    listOf(
                            Node.Assignment("a", "in"),
                            Node.Assignment("b", "in"),
                            Node.Assignment("out", "out")
                    )
            )
    )
    Node.Chip("Not", ins, outs, components)
}()

val EXPECTED_AND_TREE = {
    val ins = listOf("a", "b")
    val outs = listOf("out")
    val components = listOf(
            Node.Component(
                    "Nand",
                    listOf(
                            Node.Assignment("a", "a"),
                            Node.Assignment("b", "b"),
                            Node.Assignment("out", "nandout")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "nandout"),
                            Node.Assignment("out", "out")
                    )
            )
    )
    Node.Chip("And", ins, outs, components)
}()