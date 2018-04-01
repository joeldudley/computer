package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.*

class GeneratorImpl: Generator {
    override fun generateChip(chipNode: ChipNode): Chip {
        // Stage 1: Create a list of variables used in the chip definition,
        // including inputs and outputs.
        val chipVariableNames = getChipVariables(chipNode)

        // Step 2.1: Map each variable name to a gate.
        val chipGates = createChipGates(chipVariableNames)
        // Step 2.2: Create and hook up the chip's parts.
        generateAndHookUpParts(chipNode, chipGates.inputOutputAndInternalGates)
        // Step 2.3: Create and return chip.
        return Chip(chipGates.inputGates, chipGates.outputGates)
    }

    private data class ChipVariable(val name: String, val width: Int)
    private class ChipVariables(
            val inputVariables: List<ChipVariable>,
            val outputVariables: List<ChipVariable>,
            val internalVariables: List<ChipVariable>)

    private class ChipGates(
            val inputGates: Map<String, List<Gate>>,
            val outputGates: Map<String, List<Gate>>,
            internalGates: Map<String, List<Gate>>) {
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

        val inputGateMap = mapOf("a" to listOf(in1), "b" to listOf(in2))
        val outputGateMap = mapOf("out" to listOf(nandGate))
        return Chip(inputGateMap, outputGateMap)
    }

    private fun getChipVariables(chipNode: ChipNode): ChipVariables {
        val inputNames = chipNode.inputs.map { ChipVariable(it.name, it.width) }
        val outputNames = chipNode.outputs.map { ChipVariable(it.name, it.width) }
        // All variables used internally by the parts, including the input and
        // output variables.
        // TODO: Check for duplicates. Can we use a set?
        val assignmentRHSs = chipNode.parts.flatMap { part ->
            part.assignments.map { assignment ->
                val insAndOuts = part.chip.inputs + part.chip.outputs
                val rhsWidth = insAndOuts.find { it.name == assignment.rhs.name }!!.width
                ChipVariable(assignment.rhs.name, rhsWidth)
            }
        }
        // All variables used internally by the parts, excluding the input and
        // output variables.
        val internalVariableNames = assignmentRHSs.filter { variableName ->
            variableName !in inputNames && variableName !in outputNames
        }

        return ChipVariables(inputNames, outputNames, internalVariableNames)
    }

    // TODO: Need to do width.
    private fun createChipGates(chipVariables: ChipVariables): ChipGates {
        val inputGates = chipVariables.inputVariables.map {
            // TODO: Will this be off by one?
            (name, width) -> name to (0..width).map { PassthroughGate() }
        }.toMap()
        val outputGates = chipVariables.outputVariables.map {
            // TODO: Will this be off by one?
            (name, width) -> name to (0..width).map { PassthroughGate() }
        }.toMap()
        val internalVariableGates = chipVariables.internalVariables.map {
            // TODO: Will this be off by one?
            (name, width) -> name to (0..width).map { PassthroughGate() }
        }.toMap()
        return ChipGates(inputGates, outputGates, internalVariableGates)
    }

    private fun generateAndHookUpParts(chipNode: ChipNode, inputOutputAndInternalGates: Map<String, List<Gate>>) {
        chipNode.parts.forEach { part ->
            generateAndHookUpPart(part, inputOutputAndInternalGates)
        }
    }

    // TODO: Need to do width.
    private fun generateAndHookUpPart(partNode: PartNode, inputOutputAndInternalGates: Map<String, List<Gate>>) {
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
            // TODO: Handle indexing, rather than updating every pin.
            when (lhsName) {
                in partChip.inputGateMap -> {
                    val lhsGate = partChip.inputGateMap[lhsName]
                            ?: throw IllegalArgumentException("LHS is not input of part.")
                    for ((lhsPin, rhsPin) in lhsGate.zip(rhsGate)) {
                        lhsPin.in1 = rhsPin
                        rhsPin.outputs.add(lhsPin)
                    }
                }
                in partChip.outputGateMap -> {
                    val lhsGate = partChip.outputGateMap[lhsName]
                            ?: throw IllegalArgumentException("LHS is not output of part.")
                    for ((lhsPin, rhsPin) in lhsGate.zip(rhsGate)) {
                        rhsPin.in1 = lhsPin
                        lhsPin.outputs.add(rhsPin)
                    }
                }
                else -> {
                    throw IllegalArgumentException("LHS is neither input nor output of part.")
                }
            }
        }
    }
}