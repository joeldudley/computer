package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Correct this. Need to use handle indexes in square brackets.
class GeneratorImpl : Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        // TODO: New approach here:
        // * Get input and output variable names
        // * Create gates for the inputs only
            // * The output gates already exist as the outputs of the chips
        // * Create a list of all the parts
        // * Get the possible RHS as the input gates plus the outputs of the various chips
        // * Link them up
        // * Return input gates and output gates from chips

        val chipVariableNames = getChipVariableNames(chipNode)
        val chipInputGates = chipVariableNames.inputGateNames.map { name -> name to PassthroughGate() }.toMap()
        val parts = generateParts(chipNode)

        // TODO: Check for overlap and throw an error for sanity checking.
        val allRHSGates = mutableMapOf<String, Gate>()
        parts.forEach { part ->
            part.outputs.map { output ->
                val key = output.rhs.name
                val value = part.chip.outputGateMap[output.lhs.name]!!
                allRHSGates.put(key, value)
            }
        }
        allRHSGates.putAll(chipInputGates)

        hookUpParts(parts, allRHSGates)

        val chipOutputGates = chipVariableNames.outputGateNames.map { it to allRHSGates[it]!! }.toMap()
        return Chip(chipInputGates, chipOutputGates)
    }

    private class ChipVariableNames(
            val inputGateNames: List<String>,
            val outputGateNames: List<String>)

    private class Part(val chip: Chip, val inputs: List<AssignmentNode>, val outputs: List<AssignmentNode>)

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

    private fun getChipVariableNames(chipNode: ChipNode): ChipVariableNames {
        val inputNames = chipNode.inputs.map { it.name }
        val outputNames = chipNode.outputs.map { it.name }
        return ChipVariableNames(inputNames, outputNames)
    }

    private fun generateParts(chipNode: ChipNode): List<Part> {
        return chipNode.parts.map { partNode ->
            val chip = if (partNode.chip.name == "Nand") {
                generateNand()
            } else {
                generateChip(partNode.chip)
            }

            val inputs = mutableListOf<AssignmentNode>()
            val outputs = mutableListOf<AssignmentNode>()

            for (assignment in partNode.assignments) {
                when (assignment.lhs.name) {
                    in chip.inputGateMap -> inputs.add(assignment)
                    in chip.outputGateMap -> outputs.add(assignment)
                    else -> throw IllegalArgumentException("Assignment LHS does not exist in part inputs or outputs.")
                }
            }

            Part(chip, inputs, outputs)
        }
    }

    private fun hookUpParts(parts: List<Part>, allRHSGates: Map<String, Gate>) {
        parts.forEach { part ->
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
}