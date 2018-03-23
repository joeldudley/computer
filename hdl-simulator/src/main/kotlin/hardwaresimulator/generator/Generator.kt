package hardwaresimulator.generator

import hardwaresimulator.ChipIOGates
import hardwaresimulator.PassthroughGate
import hardwaresimulator.parser.Node

// TODO: Correct this. Need to use index, and not just name.
class Generator {
    fun generateChipFun(chip: Node.Chip, chipFuns: Map<String, () -> ChipIOGates>): () -> ChipIOGates {
        return fun(): ChipIOGates {
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
                val partGenerator = chipFuns[part.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val (partInGates, partOutGates) = partGenerator()

                part.assignments.forEach { assignment ->
                    val rhsGate = gates[assignment.rhs.name] ?: throw IllegalArgumentException("RHS not found in part assignment.")

                    when (assignment.lhs.name) {
                        in partInGates -> {
                            val lhsGate = partInGates[assignment.lhs.name]!!
                            lhsGate.in1 = rhsGate
                            rhsGate.outputs.add(lhsGate)
                        }
                        in partOutGates -> {
                            val lhsGate = partOutGates[assignment.lhs.name]!!
                            rhsGate.in1 = lhsGate
                            lhsGate.outputs.add(rhsGate)
                        }
                        else -> throw IllegalArgumentException("LHS not found in chip definition.")
                    }
                }
            }

            return ChipIOGates(
                    // TODO: Clear this up.
                    inGates.map { it.key to it.value }.toMap(),
                    outGates.map { it.key to it.value }.toMap()
            )
        }
    }
}