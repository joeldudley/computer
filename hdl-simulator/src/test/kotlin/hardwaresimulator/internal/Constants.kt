package hardwaresimulator.internal

import hardwaresimulator.internal.parser.*

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
            IOPinNode("in", 1)
    )
    val outs = listOf(
            IOPinNode("out", 1)
    )
    val parts = listOf(
            PartNode(
                    "Nand",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("a", 0),
                                    InternalPinNode("in", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("b", 0),
                                    InternalPinNode("in", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("out", 0)
                            )
                    )
            )
    )
    ChipNode("Not", ins, outs, parts)
}()

val AND_CHIP = {
    val ins = listOf(
            IOPinNode("a", 1),
            IOPinNode("b", 1)
    )
    val outs = listOf(
            IOPinNode("out", 1)
    )
    val parts = listOf(
            PartNode(
                    "Nand",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("a", 0),
                                    InternalPinNode("a", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("b", 0),
                                    InternalPinNode("b", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("nandOut", 0)
                            )
                    )
            ),
            PartNode(
                    "Not",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("in", 0),
                                    InternalPinNode("nandOut", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("out", 0)
                            )
                    )
            )
    )
    ChipNode("And", ins, outs, parts)
}()

val OR_CHIP = {
    val ins = listOf(
            IOPinNode("a", 1),
            IOPinNode("b", 1)
    )
    val outs = listOf(
            IOPinNode("out", 1)
    )
    val parts = listOf(
            PartNode(
                    "Not",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("in", 0),
                                    InternalPinNode("a", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("notA", 0)
                            )
                    )
            ),
            PartNode(
                    "Not",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("in", 0),
                                    InternalPinNode("b", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("notB", 0)
                            )
                    )
            ),
            PartNode(
                    "And",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("a", 0),
                                    InternalPinNode("notA", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("b", 0),
                                    InternalPinNode("notB", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("notAB", 0)
                            )
                    )
            ),
            PartNode(
                    "Not",
                    listOf(
                            AssignmentNode(
                                    InternalPinNode("in", 0),
                                    InternalPinNode("notAB", 0)
                            ),
                            AssignmentNode(
                                    InternalPinNode("out", 0),
                                    InternalPinNode("out", 0)
                            )
                    )
            )
    )
    ChipNode("Or", ins, outs, parts)
}()

val NOT16_CHIP = {
    val ins = listOf(
            IOPinNode("in", 16)
    )
    val outs = listOf(
            IOPinNode("out", 16)
    )
    val parts = (0..15).map {
        PartNode(
                "Not", listOf(
                AssignmentNode(
                        InternalPinNode("in", 0),
                        InternalPinNode("in", it)
                ),
                AssignmentNode(
                        InternalPinNode("out", 0),
                        InternalPinNode("out", it)
                ))
        )
    }
    ChipNode("Not16", ins, outs, parts)
}()