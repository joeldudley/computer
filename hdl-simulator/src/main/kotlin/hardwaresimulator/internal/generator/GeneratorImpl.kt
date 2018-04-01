package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        val inputGateMap = generateInputGateMap(chipNode)

        val partsAndAssignments = generatePartsAndSplitAssignments(chipNode)
        val partVariableGateMap = generateVariableGateMap(partsAndAssignments)

        val rhsGateMap = inputGateMap + partVariableGateMap

        hookUpParts(partsAndAssignments, rhsGateMap)

        val chipOutputNames = chipNode.outputs.map { outputNode -> outputNode.name }
        val outputGateMap = chipOutputNames.map { outputName -> outputName to rhsGateMap[outputName]!! }.toMap()
        return Chip(inputGateMap, outputGateMap)
    }

    private fun generateInputGateMap(chipNode: ChipNode): Map<String, Gate> {
        val inputNames = chipNode.inputs.map { inputNode -> inputNode.name }
        return inputNames.map { inputName -> inputName to PassthroughGate() }.toMap()
    }

    private fun hookUpParts(partsAndAssignments: List<PartAndAssignments>, rhsGateMap: Map<String, Gate>) {
        partsAndAssignments.forEach { part -> hookUpPart(part, rhsGateMap) }
    }

    private data class PartAndAssignments(
            val chip: Chip,
            val inputAssignments: List<AssignmentNode>,
            val outputAssignments: List<AssignmentNode>)

    private fun generatePartsAndSplitAssignments(chipNode: ChipNode): List<PartAndAssignments> {
        return chipNode.parts.map { partNode ->
            val part = generatePart(partNode)
            val partAssignments = partNode.assignments
            val (inputAssignments, outputAssignments) = splitInputAndOutputAssignments(part, partAssignments)
            PartAndAssignments(part, inputAssignments, outputAssignments)
        }
    }

    private fun generatePart(partNode: PartNode): Chip {
        return if (partNode.chip.name == "Nand") {
            generateNand()
        } else {
            generateChip(partNode.chip)
        }
    }

    private fun splitInputAndOutputAssignments(part: Chip, assignments: List<AssignmentNode>): Pair<List<AssignmentNode>, List<AssignmentNode>> {
        val inputAssignments = mutableListOf<AssignmentNode>()
        val outputAssignments = mutableListOf<AssignmentNode>()
        for (assignment in assignments) {
            when (assignment.lhs.name) {
                in part.inputGateMap -> inputAssignments += assignment
                in part.outputGateMap -> outputAssignments += assignment
                else -> throw IllegalArgumentException("Assignment LHS does not exist in part inputs or outputs.")
            }
        }

        return inputAssignments to outputAssignments
    }

    private fun generateVariableGateMap(partsAndAssignments: List<PartAndAssignments>): Map<String, Gate> {
        val variableGateMap = mutableMapOf<String, Gate>()

        for ((part, _, outputAssignments) in partsAndAssignments) {
            outputAssignments.forEach { output ->
                val key = output.rhs.name
                val value = part.outputGateMap[output.lhs.name]!!
                variableGateMap.put(key, value)
            }
        }

        return variableGateMap
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