package hardwaresimulator.generator

import hardwaresimulator.ChipIOGates
import hardwaresimulator.PassthroughGate
import hardwaresimulator.parser.Node

class Generator {
    fun generateChipFun(chip: Node.Chip, chipFuns: Map<String, () -> ChipIOGates>): () -> ChipIOGates {
        return fun(): ChipIOGates {
            // Step 1: Create a list of all the variables (the inputs, the outputs, and any internal variables used as
            // RHS's in the linking of the internal parts).
            val partVariables = chip.parts.flatMap { it.assignments.map { it.rhs } }

            // Step 2: Create a gate for each variable.
            val ins = chip.ins.map { it to PassthroughGate() }.toMap()
            val outs = chip.outs.map { it to PassthroughGate() }.toMap()
            val gates = partVariables.map { it to PassthroughGate() }.toMap() + ins + outs

            // Step 3: Hook up all the gates.
            chip.parts.forEach { part ->
                val partGenerator = chipFuns[part.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val (partInGates, partOutGates) = partGenerator()

                part.assignments.forEach { assignment ->
                    val rhsGate = gates[assignment.rhs] ?: throw IllegalArgumentException("RHS not found in part assignment.")

                    // TODO: Correct this. Need to use index, and not just name.
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
                    ins.map { it.key.name to it.value }.toMap(),
                    outs.map { it.key.name to it.value }.toMap()
            )
        }
    }
}