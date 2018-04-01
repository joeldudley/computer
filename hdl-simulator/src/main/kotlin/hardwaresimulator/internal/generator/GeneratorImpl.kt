package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        val inputGateMap = chipNode.inputs.map { assignment -> assignment.name to PassthroughGate() }.toMap()
        val parts = generateParts(chipNode)

        val allRHSGates = parts.flatMap { part ->
            part.outputs.map { output ->
                val key = output.rhs.name
                val value = part.chip.outputGateMap[output.lhs.name]!!
                key to value
            }
        }.toMap() + inputGateMap

        parts.forEach { part -> hookUpPart(part, allRHSGates) }

        val outputGateMap = chipNode.outputs.map { output -> output.name to allRHSGates[output.name]!! }.toMap()
        return Chip(inputGateMap, outputGateMap)
    }

    private data class Part(val chip: Chip, val inputs: List<AssignmentNode>, val outputs: List<AssignmentNode>)

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

    private fun generateParts(chipNode: ChipNode): List<Part> {
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

            Part(chip, inputAssignments, outputAssignments)
        }
    }

    private fun hookUpPart(part: Part, allRHSGates: Map<String, Gate>) {
        // Step 2: Hook up all the gates.
        part.inputs.forEach { assignment ->
            val rhsGate = allRHSGates[assignment.rhs.name]
                    ?: throw IllegalArgumentException("Assignment RHS not found in chip's input, output or internal gates.")

            val lhsGate = part.chip.inputGateMap[assignment.lhs.name]
                    ?: throw IllegalArgumentException("LHS is not input of part.")

            lhsGate.in1 = rhsGate
            rhsGate.outputs.add(lhsGate)
        }
    }
}