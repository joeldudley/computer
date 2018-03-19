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
val MISSING_PART_SEMICOLON_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:",
        "Nand", "(", "a", "=", "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", "}")

val MISSING_CHIP_TOKEN_TOKENS = listOf("NA")
val MISSING_IN_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "in")
val MISSING_OUT_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "out")
val MISSING_PARTS_TOKEN_TOKENS = listOf("CHIP", "NA", "{", "IN", "in", ";", "OUT", "out", ";", "Nand")

val NO_INPUTS_TOKENS = listOf("CHIP", "Not", "{", "IN", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val NO_OUTPUTS_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val NO_PARTS_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "}")

val NOT_CHIP = {
    val ins = listOf(
            Node.IOPin("in", 1)
    )
    val outs = listOf(
            Node.IOPin("out", 1)
    )
    val parts = listOf(
            Node.Part(
                    "Nand",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("a", 0),
                                    Node.Pin("in", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("b", 0),
                                    Node.Pin("in", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("out", 0)
                            )
                    )
            )
    )
    Node.Chip("Not", ins, outs, parts)
}()

val AND_CHIP = {
    val ins = listOf(
            Node.IOPin("a", 1),
            Node.IOPin("b", 1)
    )
    val outs = listOf(
            Node.IOPin("out", 1)
    )
    val parts = listOf(
            Node.Part(
                    "Nand",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("a", 0),
                                    Node.Pin("a", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("b", 0),
                                    Node.Pin("b", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("nandOut", 0)
                            )
                    )
            ),
            Node.Part(
                    "Not",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("in", 0),
                                    Node.Pin("nandOut", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("out", 0)
                            )
                    )
            )
    )
    Node.Chip("And", ins, outs, parts)
}()

val OR_CHIP = {
    val ins = listOf(
            Node.IOPin("a", 1),
            Node.IOPin("b", 1)
    )
    val outs = listOf(
            Node.IOPin("out", 1)
    )
    val parts = listOf(
            Node.Part(
                    "Not",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("in", 0),
                                    Node.Pin("a", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("notA", 0)
                            )
                    )
            ),
            Node.Part(
                    "Not",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("in", 0),
                                    Node.Pin("b", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("notB", 0)
                            )
                    )
            ),
            Node.Part(
                    "And",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("a", 0),
                                    Node.Pin("notA", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("b", 0),
                                    Node.Pin("notB", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("notAB", 0)
                            )
                    )
            ),
            Node.Part(
                    "Not",
                    listOf(
                            Node.Assignment(
                                    Node.Pin("in", 0),
                                    Node.Pin("notAB", 0)
                            ),
                            Node.Assignment(
                                    Node.Pin("out", 0),
                                    Node.Pin("out", 0)
                            )
                    )
            )
    )
    Node.Chip("Or", ins, outs, parts)
}()

val NOT16_CHIP = {
    val ins = listOf(
            Node.IOPin("in", 16)
    )
    val outs = listOf(
            Node.IOPin("out", 16)
    )
    val parts = (0..15).map {
        Node.Part(
                "Not", listOf(
                Node.Assignment(
                        Node.Pin("in", 0),
                        Node.Pin("in", it)
                ),
                Node.Assignment(
                        Node.Pin("out", 0),
                        Node.Pin("out", it)
                ))
        )
    }
    Node.Chip("Not16", ins, outs, parts)
}()