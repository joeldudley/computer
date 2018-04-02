package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        val partsAndAssignments = generateParts(chipNode)

        val inputGates = generateInputGateMap(chipNode)
        val partOutputGates = extractPartOutputGates(partsAndAssignments)
        val inputAssignmentGates = inputGates + partOutputGates

        hookUpParts(partsAndAssignments, inputAssignmentGates)

        val chipOutputNames = chipNode.outputs.map { outputNode -> outputNode.name }
        val outputGates = chipOutputNames.map { outputName -> outputName to inputAssignmentGates[outputName]!! }.toMap()

        return Chip(inputGates, outputGates)
    }

    private fun generateInputGateMap(chipNode: ChipNode): Map<String, List<Gate>> {
        val inputNames = chipNode.inputs.map { inputNode -> inputNode.name }
        return inputNames.map { inputName -> inputName to listOf(PassthroughGate()) }.toMap()
    }

    private data class PartAndAssignments(
            val chip: Chip,
            val inputAssignments: List<AssignmentNode>,
            val outputAssignments: List<AssignmentNode>)

    private fun generateParts(chipNode: ChipNode): List<PartAndAssignments> {
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

    private fun extractPartOutputGates(partsAndAssignments: List<PartAndAssignments>): Map<String, List<Gate>> {
        return partsAndAssignments.flatMap { partAndAssignments ->
            partAndAssignments.outputAssignments.map { outputAssignment ->
                val gate = partAndAssignments.chip.outputGateMap[outputAssignment.lhs.name]!!
                outputAssignment.rhs.name to gate
            }
        }.toMap()
    }

    private fun hookUpParts(partsAndAssignments: List<PartAndAssignments>, rhsGateMap: Map<String, List<Gate>>) {
        partsAndAssignments.forEach { part -> hookUpPart(part, rhsGateMap) }
    }

    private fun hookUpPart(part: PartAndAssignments, inputAssignmentGates: Map<String, List<Gate>>) {
        part.inputAssignments.forEach { assignment ->
            val rhsGate = inputAssignmentGates[assignment.rhs.name]
                    ?: throw IllegalArgumentException("Assignment RHS not found in chip's input, output or internal gates.")

            val lhsGate = part.chip.inputGateMap[assignment.lhs.name]
                    ?: throw IllegalArgumentException("LHS is not input of part.")

            // TODO: Don't use single() - index properly to handle wide chips.
            lhsGate.single().in1 = rhsGate.single()
            rhsGate.single().outputs.add(lhsGate.single())
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