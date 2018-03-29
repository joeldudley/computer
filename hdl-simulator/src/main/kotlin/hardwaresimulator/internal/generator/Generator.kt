package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.NandGate
import hardwaresimulator.internal.PassthroughGate
import hardwaresimulator.internal.parser.Node

// TODO: Correct this. Need to use index, and not just name.
class Generator {
    fun generateChip(name: String): Chip {
        val chipGenerator = chipGenerators[name] ?: throw IllegalArgumentException("Unknown chip.")
        return chipGenerator()
    }

    // A function that generates a Nand gate.
    private val nandGenerator = fun(): Chip {
        val nandGate = NandGate()

        val in1 = PassthroughGate()
        nandGate.in1 = in1
        in1.outputs.add(nandGate)

        val in2 = PassthroughGate()
        nandGate.in2 = in2
        in2.outputs.add(nandGate)

        return Chip(
                inputs = mapOf("a" to in1, "b" to in2),
                outputs = mapOf("out" to nandGate))
    }

    // Known chip generators.
    private val chipGenerators = mutableMapOf(
            "Nand" to nandGenerator
    )

    fun addChipDefinition(chip: Node.Chip) {
        chipGenerators.put(chip.name, fun(): Chip {
            // Step 1: Get the names of all the variable names in the inputs,
            // the outputs and the part RHSs.
            val inGateNames = chip.ins.map { it.name }
            val outGateNames = chip.outs.map { it.name }
            val partRHSNames = chip.parts.flatMap {
                it.assignments.map { it.rhs.name }
            }

            // Step 2: Filter out any part RHSs variables that are actually
            // inputs or outputs.
            val partNonDuplicatedRHSNames = partRHSNames.filter { rhsName ->
                rhsName !in inGateNames && rhsName !in outGateNames
            }

            // Step 3: Associate each variable name to a gate.
            val inGates = inGateNames.map { name -> name to PassthroughGate() }.toMap()
            val outGates = outGateNames.map { it to PassthroughGate() }.toMap()
            val partGates = partNonDuplicatedRHSNames
                    .map { it to PassthroughGate() }.toMap()
            val gates = inGates + outGates + partGates

            // Step 4: Hook up all the gates.
            chip.parts.forEach { part ->
                val partGenerator = chipGenerators[part.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val (partInGates, partOutGates) = partGenerator()

                part.assignments.forEach { assignment ->
                    val rhsGate = gates[assignment.rhs.name] ?: throw IllegalArgumentException("RHS not found in part assignment.")

                    when (assignment.lhs.name) {
                        in partInGates -> {
                            val lhsGate = partInGates[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
                            lhsGate.in1 = rhsGate
                            rhsGate.outputs.add(lhsGate)
                        }
                        in partOutGates -> {
                            val lhsGate = partOutGates[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
                            rhsGate.in1 = lhsGate
                            lhsGate.outputs.add(rhsGate)
                        }
                        else -> throw IllegalArgumentException("LHS not found in chip definition.")
                    }
                }
            }

            return Chip(inGates, outGates)
        })
    }
}