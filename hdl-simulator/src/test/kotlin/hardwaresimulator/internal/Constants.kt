package hardwaresimulator.internal

val NOT_HDL_TOKENS = listOf("CHIP", "Not", "{", "IN", "in", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a", "=",
        "in", ",", "b", "=", "in", ",", "out", "=", "out", ")", ";", "}")
val AND_HDL_TOKENS = listOf("CHIP", "And", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Nand", "(", "a",
        "=", "a", ",", "b", "=", "b", ",", "out", "=", "nandOut", ")", ";", "Not", "(", "in", "=", "nandOut", ",",
        "out", "=", "out", ")", ";", "}")
val OR_HDL_TOKENS = listOf("CHIP", "Or", "{", "IN", "a", ",", "b", ";", "OUT", "out", ";", "PARTS:", "Not", "(", "in",
        "=", "a", ",", "out", "=", "notA", ")", ";", "Not", "(", "in", "=", "b", ",", "out", "=", "notB", ")", ";",
        "Nand", "(", "a", "=", "notA", ",", "b", "=", "notB", ",", "out", "=", "out", ")", ";", "}")
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
    val nandNode = ChipNode("Nand", listOf(), listOf(), listOf())
    val nandAssignments = listOf(
            AssignmentNode(
                    LHSNode("a"),
                    RHSNode("in", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("b"),
                    RHSNode("in", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("out", false, null, null)
            )
    )
    val nandPartNode = PartNode(nandNode, nandAssignments)

    val notIns = listOf(IONode("in", 1))
    val notOuts = listOf(IONode("out", 1))
    val notParts = listOf(nandPartNode)
    val notNode = ChipNode("Not", notIns, notOuts, notParts)

    notNode
}()

val AND_CHIP = {
    val nandNode = ChipNode("Nand", listOf(), listOf(), listOf())
    val nandAssignments = listOf(
            AssignmentNode(
                    LHSNode("a"),
                    RHSNode("a", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("b"),
                    RHSNode("b", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("nandOut", false, null, null)
            )
    )
    val nandPartNode = PartNode(nandNode, nandAssignments)

    val notAssignments = listOf(
            AssignmentNode(
                    LHSNode("in"),
                    RHSNode("nandOut", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("out", false, null, null)
            )
    )
    val notPartNode = PartNode(NOT_CHIP, notAssignments)

    val andIns = listOf(
            IONode("a", 1),
            IONode("b", 1)
    )
    val andOuts = listOf(
            IONode("out", 1)
    )
    val andParts = listOf(nandPartNode, notPartNode)
    val andNode = ChipNode("And", andIns, andOuts, andParts)

    andNode
}()

val OR_CHIP = {
    val not1Assignments = listOf(
            AssignmentNode(
                    LHSNode("in"),
                    RHSNode("a", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("notA", false, null, null)
            )
    )
    val not1PartNode = PartNode(NOT_CHIP, not1Assignments)

    val not2Assignments = listOf(
            AssignmentNode(
                    LHSNode("in"),
                    RHSNode("b", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("notB", false, null, null)
            )
    )
    val not2PartNode = PartNode(NOT_CHIP, not2Assignments)

    val nandNode = ChipNode("Nand", listOf(), listOf(), listOf())
    val nandAssignments = listOf(
            AssignmentNode(
                    LHSNode("a"),
                    RHSNode("notA", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("b"),
                    RHSNode("notB", false, null, null)
            ),
            AssignmentNode(
                    LHSNode("out"),
                    RHSNode("out", false, null, null)
            )
    )
    val nandPartNode = PartNode(nandNode, nandAssignments)

    val orIns = listOf(
            IONode("a", 1),
            IONode("b", 1)
    )
    val orOuts = listOf(
            IONode("out", 1)
    )
    val orParts = listOf(not1PartNode, not2PartNode, nandPartNode)
    val orNode = ChipNode("Or", orIns, orOuts, orParts)

    orNode
}()

val NOT16_CHIP = {
    val not16Ins = listOf(
            IONode("in", 16)
    )
    val not16Outs = listOf(
            IONode("out", 16)
    )
    val not16Parts = (0..15).map {
        val notAssignments = listOf(
                AssignmentNode(
                        LHSNode("in"),
                        RHSNode("in", true, it, it)
                ),
                AssignmentNode(
                        LHSNode("out"),
                        RHSNode("out", true, it, it)
                ))
        PartNode(NOT_CHIP, notAssignments)
    }
    val not16Node = ChipNode("Not16", not16Ins, not16Outs, not16Parts)

    not16Node
}()