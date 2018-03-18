package hardwaresimulator

import hardwaresimulator.parser.Node

val NOT_HDL_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val AND_HDL_TOKENS = listOf("CHIP", "And", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "a", ",", "b", "=", "b", ",", "out", "=", "nandOut", ")", ";", "Not", "(", "in", "=", "nandOut", ",",
        "out", "=", "out", ")", ";", "}")
val OR_HDL_TOKENS = listOf("CHIP", "Or", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Not", "(", "in",
        "=", "a", ",", "out", "=", "notA", ")", ";", "Not", "(", "in", "=", "b", ",", "out", "=", "notB", ")", ";",
        "And", "(", "a", "=", "notA", ",", "b", "=", "notB", ",", "out", "=", "notAB", ")", ";", "Not", "(", "in", "=",
        "notAB", ",", "out", "=", "out", ")", ";", "}")
val NOT16_HDL_TOKENS = listOf("CHIP", "Not16", "{", "IN", "in", "[", "16", "]", ";", "OUT", "out", "[", "16", "]", ";", "PARTS:") +
        (0..15).flatMap { listOf("Not", "(", "in", "=", "in", "[", "$it", "]", ",", "out", "=", "out", "[", "$it", "]", ")", ";") } +
        listOf("}")

val MISSING_INS_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", "}")
val MISSING_OUTS_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", "PARTS:", "Nand", "(",
        "a", "=", "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", "}")
val MISSING_COMPONENT_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:",
        "Nand", "(", "a", "=", "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", "}")

val MISSING_CHIP_TOKEN_TOKENS = listOf("NA")
val MISSING_IN_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "in")
val MISSING_OUT_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "out")
val MISSING_PARTS_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", ";", "Nand")

val NO_INPUTS_TOKENS = listOf("CHIP", "Not", "{", "IN", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val NO_OUTPUTS_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val NO_COMPONENTS_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "}")

val NOT_CHIP = {
    val ins = listOf(
            Node.Pin("in", 1)
    )
    val outs = listOf(
            Node.Pin("out", 1)
    )
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
    val ins = listOf(
            Node.Pin("a", 1),
            Node.Pin("b", 1)
    )
    val outs = listOf(
            Node.Pin("out", 1)
    )
    val components = listOf(
            Node.Component(
                    "Nand",
                    listOf(
                            Node.Assignment("a", "a"),
                            Node.Assignment("b", "b"),
                            Node.Assignment("out", "nandOut")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "nandOut"),
                            Node.Assignment("out", "out")
                    )
            )
    )
    Node.Chip("And", ins, outs, components)
}()

val OR_CHIP = {
    val ins = listOf(
            Node.Pin("a", 1),
            Node.Pin("b", 1)
    )
    val outs = listOf(
            Node.Pin("out", 1)
    )
    val components = listOf(
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "a"),
                            Node.Assignment("out", "notA")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "b"),
                            Node.Assignment("out", "notB")
                    )
            ),
            Node.Component(
                    "And",
                    listOf(
                            Node.Assignment("a", "notA"),
                            Node.Assignment("b", "notB"),
                            Node.Assignment("out", "notAB")
                    )
            ),
            Node.Component(
                    "Not",
                    listOf(
                            Node.Assignment("in", "notAB"),
                            Node.Assignment("out", "out")
                    )
            )
    )
    Node.Chip("Or", ins, outs, components)
}()

// TODO: Update.
val NOT16_CHIP = {
    val ins = listOf(
            Node.Pin("in", 16)
    )
    val outs = listOf(
            Node.Pin("out", 16)
    )
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