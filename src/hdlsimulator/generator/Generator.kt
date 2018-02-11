package hdlsimulator.generator

import hdlsimulator.Gate
import hdlsimulator.NandGate
import hdlsimulator.RegGate
import hdlsimulator.parser.Node

data class HookUps(val ins: Map<String, Gate>, val outs: Map<String, Gate>)

class Generator {
    val knownChips = mutableMapOf<String, () -> HookUps>()

    fun convertNodeToGates(chip: Node.Chip) {
        // Step 1: Create a list of all the variables (the inputs, the outputs, and any internal variables used as
        // RHS's in the linking of the internal components).
        val componentVariables = chip.components.flatMap { it.assignments.map { it.rhs } }

        val createHookups = fun(): HookUps {
            // Step 2: Create a gate for each variable.
            val ins = chip.ins.map { it to RegGate() }.toMap()
            val outs = chip.outs.map { it to RegGate() }.toMap()
            val gates = componentVariables.map { it to RegGate() }.toMap() + ins + outs

            // Step 3: Hook up all the gates.
            for (component in chip.components) {
                when (component.name) {
                    "Nand" -> {
                        val nandGate = NandGate()

                        for (assignment in component.assignments) {
                            val rhsGate = gates[assignment.rhs] ?: gates[assignment.rhs]!!
                            when (assignment.lhs) {
                                "a" -> {
                                    nandGate.input = rhsGate
                                    rhsGate.outputs.add(nandGate)
                                }
                                "b" -> {
                                    nandGate.auxInput = rhsGate
                                    rhsGate.outputs.add(nandGate)
                                }
                                "out" -> {
                                    nandGate.outputs.add(rhsGate)
                                    rhsGate.input = nandGate
                                }
                            }
                        }
                    }

                    else -> {
                        val componentChipFun = knownChips[component.name] ?: throw IllegalArgumentException("Unsupported chip.")
                        val (inVars, outVars) = componentChipFun()

                        for (assignment in component.assignments) {
                            val rhsGate = gates[assignment.rhs] ?: gates[assignment.rhs]!!

                            if (assignment.lhs in inVars) {
                                val lhsGate = inVars[assignment.lhs]!!
                                lhsGate.input = rhsGate
                                rhsGate.outputs.add(lhsGate)
                            } else {
                                val lhsGate = outVars[assignment.lhs]!!
                                rhsGate.input = lhsGate
                                lhsGate.outputs.add(rhsGate)
                            }
                        }
                    }
                }
            }

            return HookUps(ins, outs)
        }

        knownChips.put(chip.name, createHookups)
    }
}