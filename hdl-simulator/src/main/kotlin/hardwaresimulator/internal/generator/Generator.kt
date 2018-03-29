package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.ChipGateMaps
import hardwaresimulator.internal.NandGate
import hardwaresimulator.internal.PassthroughGate
import hardwaresimulator.internal.parser.Node

// TODO: Correct this. Need to use index, and not just name.
class Generator {
    // Known chip generators. Defaults to only Nand.
    private val chipGenerators = mutableMapOf("Nand" to fun(): Chip {
        val nandGate = NandGate()

        val in1 = PassthroughGate()
        nandGate.in1 = in1
        in1.outputs.add(nandGate)

        val in2 = PassthroughGate()
        nandGate.in2 = in2
        in2.outputs.add(nandGate)

        return Chip(mapOf("a" to in1, "b" to in2), mapOf("out" to nandGate))
    })

    fun generateChip(name: String): Chip {
        val chipGenerator = chipGenerators[name] ?: throw IllegalArgumentException("Unknown chip.")
        return chipGenerator()
    }

    fun addChipGenerator(chipNode: Node.ChipNode) {
        // Step 1: Create a new chip generator.
        fun chipGenerator(): Chip {
            // Step 1.1: Map each variable name to a gate.
            val chipGateMaps = chipNodeToChipGateMaps(chipNode)
            val chip = Chip(chipGateMaps.inputGateMap, chipGateMaps.outputGateMap)
            val allGateMap = chipGateMaps.inputGateMap + chipGateMaps.outputGateMap + chipGateMaps.uniqueInternalVariableGates

            // Step 1.2: Create chips for each part and hook up all the gates.
            chipNode.parts.forEach { part ->
                // Step 1.2.1: Create chips for each part.
                val partGenerator = chipGenerators[part.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val partChip = partGenerator()

                // Step 1.2.2: Hook up all the gates.
                part.assignments.forEach { assignment ->
                    val rhsGate = allGateMap[assignment.rhs.name] ?: throw IllegalArgumentException("RHS not found in part assignment.")

                    when (assignment.lhs.name) {
                        in partChip.inputGateMap -> {
                            val lhsGate = partChip.inputGateMap[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
                            lhsGate.in1 = rhsGate
                            rhsGate.outputs.add(lhsGate)
                        }
                        in partChip.outputGateMap -> {
                            val lhsGate = partChip.outputGateMap[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
                            rhsGate.in1 = lhsGate
                            lhsGate.outputs.add(rhsGate)
                        }
                        else -> throw IllegalArgumentException("LHS not found in chip definition.")
                    }
                }
            }

            return chip
        }

        chipGenerators.put(chipNode.name, ::chipGenerator)
    }

    private fun chipNodeToChipGateMaps(chipNode: Node.ChipNode): ChipGateMaps {
        val inputNames = chipNode.inputs.map { it.name }
        val outputNames = chipNode.outputs.map { it.name }
        // All variables used internally by the parts, including the input and
        // output variables.
        val internalVariableNames = chipNode.parts.flatMap { part ->
            part.assignments.map { it.rhs.name }
        }
        // All variables used internally by the parts, excluding the input and
        // output variables.
        val uniqueInternalVariableNames = internalVariableNames.filter { variableName ->
            variableName !in inputNames && variableName !in outputNames
        }

        val inputGates = inputNames.map { name -> name to PassthroughGate() }.toMap()
        val outputGates = outputNames.map { name -> name to PassthroughGate() }.toMap()
        val uniqueInternalVariableGates = uniqueInternalVariableNames.map { name -> name to PassthroughGate() }.toMap()

        return ChipGateMaps(inputGates, outputGates, uniqueInternalVariableGates)
    }
}