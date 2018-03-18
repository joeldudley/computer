package hardwaresimulator

import hardwaresimulator.parser.Node

val NOT_HDL_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val AND_HDL_TOKENS = listOf("CHIP", "And", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "a", ",", "b", "=", "b", ",", "out", "=", "nandout", ")", ";", "Not", "(", "in", "=", "nandout", ",",
        "out", "=", "out", ")", ";", "}")
val OR_HDL_TOKENS = listOf("CHIP", "Or", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Not", "(", "in",
        "=", "a", ",", "out", "=", "nota", ")", ";", "Not", "(", "in", "=", "b", ",", "out", "=", "notb", ")", ";",
        "And", "(", "a", "=", "nota", ",", "b", "=", "notb", ",", "out", "=", "notab", ")", ";", "Not", "(", "in", "=",
        "notab", ",", "out", "=", "out", ")", ";", "}")
val NOT16_HDL_TOKENS = listOf("CHIP", "Not16", "{", "IN", "in", "[", "16", "]", ";", "OUT", "out", "[", "16", "]", ";", "PARTS:") +
        (0..15).flatMap { listOf("Not", "(", "in", "=", "in", "[", "$it", "]", ",", "out", "=", "out", "[", "$it", "]", ")", ";") } +
        listOf("}")

val MISSING_INS_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", "}")
val MISSING_OUTS_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", "PARTS:", "Nand", "(", "a",
        "=", "in", "b", "=", "in", "out", "=", "out", ")", "}")
val MISSING_COMPONENT_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:",
        "Nand", "(", "a", "=", "in", "b", "=", "in", "out", "=", "out", ")", "}")

val NOT_CHIP = {
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

val AND_CHIP = {
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

val OR_CHIP = {
    val ins = listOf("a", "b")
    val outs = listOf("out")
    val components = listOf(
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "a"),
                            Node.Assignment("out", "nota")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "b"),
                            Node.Assignment("out", "notb")
                    )
            ),
            Node.Component(
                    "And",
                    listOf(
                            Node.Assignment("a", "nota"),
                            Node.Assignment("b", "notb"),
                            Node.Assignment("out", "notab")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "notab"),
                            Node.Assignment("out", "out")
                    )
            )
    )
    Node.Chip("Or", ins, outs, components)
}()