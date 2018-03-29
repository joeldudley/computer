package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
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

    fun addChipGenerator(chipNode: Node.Chip) {
        // Step 1: Get the names of all the variable in the inputs, the
        // outputs and the part RHSs.
        val inputVariableNames = chipNode.ins.map { it.name }
        val outputVariableNames = chipNode.outs.map { it.name }
        val partRhsNames = chipNode.parts.flatMap { part ->
            part.assignments.map { it.rhs.name }
        }

        // Step 2: Filter out any part RHSs that are actually
        // inputs or outputs.
        val uniquePartRhsNames = partRhsNames.filter { rhsName ->
            rhsName !in inputVariableNames && rhsName !in outputVariableNames
        }

        // Step 3: Create a new chip generator.
        fun chipGenerator(): Chip {
            // Step 3.1: Map each variable name to a gate.
            val inputGateMap = inputVariableNames.map { name -> name to PassthroughGate() }.toMap()
            val outputGateMap = outputVariableNames.map { name -> name to PassthroughGate() }.toMap()
            val partGateMap = uniquePartRhsNames
                    .map { name -> name to PassthroughGate() }.toMap()
            val gateMap = inputGateMap + outputGateMap + partGateMap

            // Step 3.2: Create chips for each part and hook up all the gates.
            chipNode.parts.forEach { part ->
                // Step 3.2.1: Create chips for each part.
                val partGenerator = chipGenerators[part.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val partChip = partGenerator()

                // Step 3.2.2: Hook up all the gates.
                part.assignments.forEach { assignment ->
                    val rhsGate = gateMap[assignment.rhs.name] ?: throw IllegalArgumentException("RHS not found in part assignment.")

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

            return Chip(inputGateMap, outputGateMap)
        }

        chipGenerators.put(chipNode.name, ::chipGenerator)
    }
}