package hdlsimulator.generator

import hdlsimulator.ChipIOGates
import hdlsimulator.Gate
import hdlsimulator.parser.Node

class Generator {
    fun generateChipFun(chip: Node.Chip, chipFuns: Map<String, () -> ChipIOGates>): () -> ChipIOGates {
        return fun(): ChipIOGates {
            // Step 1: Create a list of all the variables (the inputs, the outputs, and any internal variables used as
            // RHS's in the linking of the internal components).
            val componentVariables = chip.components.flatMap { it.assignments.map { it.rhs } }

            // Step 2: Create a gate for each variable.
            val ins = chip.ins.map { it to Gate() }.toMap()
            val outs = chip.outs.map { it to Gate() }.toMap()
            val gates = componentVariables.map { it to Gate() }.toMap() + ins + outs

            // Step 3: Hook up all the gates.
            chip.components.forEach { component ->
                val componentGenerator = chipFuns[component.name] ?: throw IllegalArgumentException("Unsupported chip.")
                val (componentInGates, componentOutGates) = componentGenerator()

                component.assignments.forEach { assignment ->
                    val rhsGate = gates[assignment.rhs] ?: throw IllegalArgumentException("RHS not found in component assignment.")

                    when (assignment.lhs) {
                        in componentInGates -> {
                            val lhsGate = componentInGates[assignment.lhs]!!
                            lhsGate.input = rhsGate
                            rhsGate.outputs.add(lhsGate)
                        }
                        in componentOutGates -> {
                            val lhsGate = componentOutGates[assignment.lhs]!!
                            rhsGate.input = lhsGate
                            lhsGate.outputs.add(rhsGate)
                        }
                        else -> throw IllegalArgumentException("LHS not found in chip definition.")
                    }
                }
            }

            return ChipIOGates(ins, outs)
        }
    }
}