package hdlsimulator.evaluator

import hdlsimulator.Gate

class Evaluator {
    fun evaluate(inputs: List<Pair<Gate, Boolean>>) {
        val outputs = mutableListOf<Gate>()
        for ((gate, newValue) in inputs) {
            gate.value = newValue
            outputs.addAll(gate.outputs)
        }

        var i = 0
        while (i < outputs.size) {
            val output = outputs[i]
            output.update()
            outputs.addAll(output.outputs)
            i++
        }
    }
}