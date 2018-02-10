package hdlsimulator.generator

import hdlsimulator.Gate
import hdlsimulator.NandGate
import hdlsimulator.RegGate
import hdlsimulator.parser.Node

data class HookUps(val ins: Map<String, Gate>, val outs: Map<String, Gate>)

// TODO: Test this - it was a first pass but it might work.
class Generator {
    fun convertNodeToGates(chip: Node.Chip): HookUps {
        val ins = chip.ins.map { it to RegGate() }.toMap()
        val outs = chip.outs.map { it to RegGate() }.toMap()

        for (component in chip.components) {
            when (component.name) {
                "Nand" -> {
                    val nandGate = NandGate()

                    for (assignment in component.assignments) {
                        when (assignment.lhs) {
                            "a" -> {
                                ins[assignment.rhs]!!.outputs.add(nandGate.input)
                                nandGate.input = ins[assignment.rhs]!!
                            }
                            "b" -> {
                                ins[assignment.rhs]!!.outputs.add(nandGate.auxInput)
                                nandGate.auxInput = ins[assignment.rhs]!!
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