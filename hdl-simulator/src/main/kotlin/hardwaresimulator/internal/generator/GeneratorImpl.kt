package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

// TODO: Correct this. Need to use handle indexes in square brackets.
class GeneratorImpl: Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        // Stage 1: Create a list of variables used in the chip definition,
        // including inputs and outputs.
        val chipVariableNames = getChipVariableNames(chipNode)

        // Step 2.1: Map each variable name to a gate.
        val chipGates = createChipGates(chipVariableNames)
        // Step 2.2: Create and hook up the chip's parts.
        generateAndHookUpParts(chipNode, chipGates.inputOutputAndInternalGates)
        // Step 2.3: Create and return chip.
        return Chip(chipGates.inputGates, chipGates.outputGates)
    }

    private class ChipVariableNames(
            val inputGateNames: List<String>,
            val outputGateNames: List<String>,
            val internalGateNames: List<String>)

    private class ChipGates(
            val inputGates: Map<String, Gate>,
            val outputGates: Map<String, Gate>,
            internalGates: Map<String, Gate>) {
        val inputOutputAndInternalGates = inputGates + outputGates + internalGates
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

    private fun getChipVariableNames(chipNode: ChipNode): ChipVariableNames {
        val inputNames = chipNode.inputs.map { it.name }
        val outputNames = chipNode.outputs.map { it.name }
        // All variables used internally by the parts, including the input and
        // output variables.
        val assignmentRHSs = chipNode.parts.flatMap { part ->
            part.assignments.map { it.rhs.name }
        }
        // All variables used internally by the parts, excluding the input and
        // output variables.
        val internalVariableNames = assignmentRHSs.filter { variableName ->
            variableName !in inputNames && variableName !in outputNames
        }

        return ChipVariableNames(inputNames, outputNames, internalVariableNames)
    }

    private fun createChipGates(chipVariableNames: ChipVariableNames): ChipGates {
        val inputGates = chipVariableNames.inputGateNames.map { name -> name to PassthroughGate() }.toMap()
        val outputGates = chipVariableNames.outputGateNames.map { name -> name to PassthroughGate() }.toMap()
        val internalVariableGates = chipVariableNames.internalGateNames.map { name -> name to PassthroughGate() }.toMap()
        return ChipGates(inputGates, outputGates, internalVariableGates)
    }

    private fun generateAndHookUpParts(chipNode: ChipNode, inputOutputAndInternalGates: Map<String, Gate>) {
        chipNode.parts.forEach { part ->
            generateAndHookUpPart(part, inputOutputAndInternalGates)
        }
    }

    private fun generateAndHookUpPart(partNode: PartNode, inputOutputAndInternalGates: Map<String, Gate>) {
        // Step 1: Generate a chip for the part.
        val partChip = if (partNode.chip.name == "Nand") {
            generateNand()
        } else {
            generateChip(partNode.chip)
        }

        // TODO: At this stage, check that the widths are correct - rhs width == lhs width

        // Step 2: Hook up all the gates.
        partNode.assignments.forEach { assignment ->
            val rhsName = assignment.rhs.name
            val rhsGate = inputOutputAndInternalGates[rhsName]
                    ?: throw IllegalArgumentException("Assignment RHS not found in chip's input, output or internal gates.")

            val lhsName = assignment.lhs.name
            when (lhsName) {
                in partChip.inputGateMap -> {
                    val lhsGate = partChip.inputGateMap[lhsName]
                            ?: throw IllegalArgumentException("LHS is not input of part.")
                    lhsGate.in1 = rhsGate
                    rhsGate.outputs.add(lhsGate)
                }
                in partChip.outputGateMap -> {
                    val lhsGate = partChip.outputGateMap[lhsName]
                            ?: throw IllegalArgumentException("LHS is not output of part.")
                    rhsGate.in1 = lhsGate
                    lhsGate.outputs.add(rhsGate)
                }
                else -> {
                    throw IllegalArgumentException("LHS is neither input nor output of part.")
                }
            }
        }
    }
}