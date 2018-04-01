package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        val chipInputNames = chipNode.inputs.map { inputNode -> inputNode.name }
        val chipInputGateMap = chipInputNames.map { inputName -> inputName to PassthroughGate() }.toMap()
        val partChipsAndAssignments = extractPartsAndAssignments(chipNode)

        val partOutputRHSGateMap = extractPartOutputRHSGateMap(partChipsAndAssignments)

        val allRHSGateMap = chipInputGateMap + partOutputRHSGateMap

        partChipsAndAssignments.forEach { part -> hookUpPart(part, allRHSGateMap) }

        val chipOutputNames = chipNode.outputs.map { outputNode -> outputNode.name }
        val outputGateMap = chipOutputNames.map { outputName -> outputName to allRHSGateMap[outputName]!! }.toMap()
        return Chip(chipInputGateMap, outputGateMap)
    }

    private data class PartAndAssignments(
            val chip: Chip,
            val inputAssignments: List<AssignmentNode>,
            val outputAssignments: List<AssignmentNode>)

    private fun extractPartsAndAssignments(chipNode: ChipNode): List<PartAndAssignments> {
        return chipNode.parts.map { partNode ->
            val chip = if (partNode.chip.name == "Nand") {
                generateNand()
            } else {
                generateChip(partNode.chip)
            }

            val inputAssignments = mutableListOf<AssignmentNode>()
            val outputAssignments = mutableListOf<AssignmentNode>()
            for (assignment in partNode.assignments) {
                when (assignment.lhs.name) {
                    in chip.inputGateMap -> inputAssignments += assignment
                    in chip.outputGateMap -> outputAssignments += assignment
                    else -> throw IllegalArgumentException("Assignment LHS does not exist in part inputs or outputs.")
                }
            }

            PartAndAssignments(chip, inputAssignments, outputAssignments)
        }
    }

    private fun extractPartOutputRHSGateMap(partChipsAndAssignments: List<PartAndAssignments>): Map<String, Gate> {
        return partChipsAndAssignments.flatMap { part ->
            part.outputAssignments.map { output ->
                val key = output.rhs.name
                val value = part.chip.outputGateMap[output.lhs.name]!!
                key to value
            }
        }.toMap()
    }

    private fun hookUpPart(part: PartAndAssignments, allRHSGates: Map<String, Gate>) {
        part.inputAssignments.forEach { assignment ->
            val rhsGate = allRHSGates[assignment.rhs.name]
                    ?: throw IllegalArgumentException("Assignment RHS not found in chip's input, output or internal gates.")

            val lhsGate = part.chip.inputGateMap[assignment.lhs.name]
                    ?: throw IllegalArgumentException("LHS is not input of part.")

            lhsGate.in1 = rhsGate
            rhsGate.outputs.add(lhsGate)
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

        val inputGateMap = mapOf("a" to in1, "b" to in2)
        val outputGateMap = mapOf("out" to nandGate)
        return Chip(inputGateMap, outputGateMap)
    }
}