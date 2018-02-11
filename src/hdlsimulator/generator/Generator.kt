package hdlsimulator.generator

import hdlsimulator.Gate
import hdlsimulator.NandGate
import hdlsimulator.RegGate
import hdlsimulator.parser.Node

data class HookUps(val ins: Map<String, Gate>, val outs: Map<String, Gate>)

class Generator {
    val knownChips = mutableMapOf<String, () -> HookUps>()

    fun convertNodeToGates(chip: Node.Chip) {
        val createHookups = fun(): HookUps {
            // Step 1: Create gates for all the variables involved in the chip definition.
            val componentVariables = chip.components.flatMap { it.assignments.map { it.rhs } }
            val allVariables = componentVariables + chip.ins + chip.outs
            val gates = allVariables.map { it to RegGate() }.toMap()

            // Step 2: Hook up all the gates.
            for (component in chip.components) {
                when (component.name) {
                    "Nand" -> {
                        val nandGate = NandGate()

                        for (assignment in component.assignments) {
                            val gate = gates[assignment.rhs] ?: gates[assignment.rhs]!!
                            when (assignment.lhs) {
                                "a" -> {
                                    nandGate.input = gate
                                    gate.outputs.add(nandGate)
                                }
                                "b" -> {
                                    nandGate.auxInput = gate
                                    gate.outputs.add(nandGate)
                                }
                                "out" -> {
                                    nandGate.outputs.add(gate)
                                    gate.input = nandGate
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

            val ins = gates.filterKeys { it in chip.ins }
            val outs = gates.filterKeys { it in chip.outs }
            return HookUps(ins, outs)
        }

        knownChips.put(chip.name, createHookups)
    }
}