package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        val chipPartsAndAssignments = generateChipPartsAndGatherPartAssignments(chipNode)

        val chipInputGates = generateChipInputGateMap(chipNode)
        val chipOutputGates = generateChipOutputGateMap(chipNode)
        val outputGatesInParts = extractPartOutputGates(chipOutputGates, chipPartsAndAssignments)
        val inputAssignmentGates = chipInputGates + outputGatesInParts

        hookUpParts(chipPartsAndAssignments, inputAssignmentGates)

        // TODO: Remove this ugly cast.
        return Chip(chipInputGates, chipOutputGates as Map<String, List<Gate>>)
    }

    private fun generateChipInputGateMap(chipNode: ChipNode): Map<String, List<Gate>> {
        return chipNode.inputs.map { input ->
            val inputGates = (0 until input.width).map { PassthroughGate() }
            input.name to inputGates
        }.toMap()
    }

    private fun generateChipOutputGateMap(chipNode: ChipNode): Map<String, MutableList<Gate?>> {
        return chipNode.outputs.map { output ->
            // The pins in the output gates are set to null initially.
            // They will be set to actual gates when walking through the
            // outputs of the chip's parts.
            val outputGates: MutableList<Gate?> = (0 until output.width).map { null }.toMutableList()
            output.name to outputGates
        }.toMap()
    }

    private data class PartAndAssignments(
            val chip: Chip,
            val inputAssignments: List<AssignmentNode>,
            val outputAssignments: List<AssignmentNode>)

    private fun generateChipPartsAndGatherPartAssignments(chipNode: ChipNode): List<PartAndAssignments> {
        return chipNode.parts.map { partNode ->
            val part = generatePart(partNode)
            PartAndAssignments(part, partNode.inputAssignments, partNode.outputAssignments)
        }
    }

    private fun generatePart(partNode: PartNode): Chip {
        return if (partNode.chip.name == "Nand") {
            generateNand()
        } else {
            generateChip(partNode.chip)
        }
    }

    private fun extractPartOutputGates(chipOutputGates: Map<String, MutableList<Gate?>>, partsAndAssignments: List<PartAndAssignments>): Map<String, List<Gate>> {
        val outputGatesInParts = mutableMapOf<String, List<Gate>>()

        partsAndAssignments.forEach { partAndAssignments ->
            partAndAssignments.outputAssignments.forEach { outputAssignment ->
                val gate = partAndAssignments.chip.outputGateMap[outputAssignment.lhs.name]!!

                // If the rhs is indexed, modify the existing pin list in the gate map.
                if (outputAssignment.rhs.name in chipOutputGates) {

                    val outputGateToUpdate = chipOutputGates[outputAssignment.rhs.name]!!

                    // TODO: Handle elipsis in the indexing.
                    // TODO: Handle cases where there isn't a single pin in the lhs gate.
                    if (outputAssignment.rhs.indexed) {
                        outputGateToUpdate[outputAssignment.rhs.startIndex!!] = gate.single()
                    } else {
                        outputGateToUpdate[0] = gate.single()
                    }
                }

                else {
                    outputGatesInParts.put(outputAssignment.rhs.name, gate)
                }
            }
        }

        return outputGatesInParts
    }

    private fun hookUpParts(partsAndAssignments: List<PartAndAssignments>, rhsGateMap: Map<String, List<Gate>>) {
        partsAndAssignments.forEach { part -> hookUpPart(part, rhsGateMap) }
    }

    private fun hookUpPart(part: PartAndAssignments, inputAssignmentGates: Map<String, List<Gate>>) {
        part.inputAssignments.forEach { assignment ->
            val lhsGates = part.chip.inputGateMap[assignment.lhs.name]
                    ?: throw IllegalArgumentException("LHS is not input of part.")

            val allRHSGates = inputAssignmentGates[assignment.rhs.name]
                    ?: throw IllegalArgumentException("Assignment RHS not found in chip's input, output or internal gates.")

            // TODO: Check if I'm doing the indexing right.
            val actualGates = if (assignment.rhs.startIndex == null && assignment.rhs.endIndex == null) {
                allRHSGates
            } else if (assignment.rhs.startIndex != null && assignment.rhs.endIndex == null) {
                allRHSGates.subList(assignment.rhs.startIndex, assignment.rhs.startIndex + 1)
            } else if (assignment.rhs.startIndex != null && assignment.rhs.endIndex != null) {
                allRHSGates.subList(assignment.rhs.startIndex, assignment.rhs.endIndex + 1)
            } else {
                throw IllegalArgumentException("Start index not specified but end index specified.")
            }

            for ((lhsGate, rhsGate) in lhsGates.zip(actualGates)) {
                lhsGate.in1 = rhsGate
                rhsGate.outputs.add(lhsGate)
            }
        }
    }

    private fun generateNand(): Chip {
        val nandGate = NandGate()

        val in1 = PassthroughGate()
        nandGate.in1 = in1
        in1.outputs.add(nandGate)

        val in2 = PassthroughGate()
        nandGate.in2 = in2
        in2.outputs.add(nandGate)

        val inputGateMap = mapOf("a" to listOf(in1), "b" to listOf(in2))
        val outputGateMap = mapOf("out" to listOf(nandGate))
        return Chip(inputGateMap, outputGateMap)
    }
}