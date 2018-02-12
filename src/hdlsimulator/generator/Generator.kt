package hdlsimulator.generator

import hdlsimulator.Gate
import hdlsimulator.NandGate
import hdlsimulator.parser.Node

data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

val generateNandGate = fun(): ChipIOGates {
    val nandGate = NandGate()

    val input = Gate()
    nandGate.input = input
    input.outputs.add(nandGate)

    val auxInput = Gate()
    nandGate.auxInput = auxInput
    auxInput.outputs.add(nandGate)

    val ins = mapOf("a" to input, "b" to auxInput)
    val outs = mapOf("out" to nandGate)
    return ChipIOGates(ins, outs)
}

class Generator {
    val chipGenerators = mutableMapOf("Nand" to generateNandGate)

    fun installChipGenerator(chip: Node.Chip) {
        // Step 1: Create a list of all the variables (the inputs, the outputs, and any internal variables used as
        // RHS's in the linking of the internal components).
        val componentVariables = chip.components.flatMap { it.assignments.map { it.rhs } }

        val chipGenerator = fun(): ChipIOGates {
            // Step 2: Create a gate for each variable.
            val ins = chip.ins.map { it to Gate() }.toMap()
            val outs = chip.outs.map { it to Gate() }.toMap()
            val gates = componentVariables.map { it to Gate() }.toMap() + ins + outs

            // Step 3: Hook up all the gates.
            chip.components.forEach { component ->
                val componentGenerator = chipGenerators[component.name] ?: throw IllegalArgumentException("Unsupported chip.")
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

        chipGenerators.put(chip.name, chipGenerator)
    }
}