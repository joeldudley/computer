package hdlsimulator.generator

import hdlsimulator.Gate
import hdlsimulator.NandGate
import hdlsimulator.RegGate
import hdlsimulator.parser.Node

data class HookUps(val ins: Map<String, Gate>, val outs: Map<String, Gate>)

class Generator {
    fun convertNodeToGates(chip: Node.Chip): HookUps {
        val ins = chip.ins.map { it to RegGate() }.toMap()
        val outs = chip.outs.map { it to RegGate() }.toMap()

        for (component in chip.components) {
            when (component.name) {
                "Nand" -> {
                    val nandGate = NandGate()

                    for (assignment in component.assignments) {
                        val gate = ins[assignment.rhs] ?: outs[assignment.rhs]!!
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
                // TODO: Extend this later to support custom gates.
                else -> throw IllegalArgumentException("Unsupported chip.")
            }
        }

        return HookUps(ins, outs)
    }
}