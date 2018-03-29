package hardwaresimulator.evaluator

import hardwaresimulator.ChipIOGates
import hardwaresimulator.Gate

val LOOPS_TO_INITIALISE = 3

class Evaluator {
    // The chip currently being simulated.
    private var loadedChip: ChipIOGates? = null

    fun loadChip(chip: ChipIOGates) {
        loadedChip = chip
        initialiseChip()
    }

    fun setInputs(inputs: List<Pair<String, Boolean>>) {
        val inputGates = loadedChip?.inGates ?: throw IllegalStateException("No chip loaded in the simulator.")

        // For each input gate, if its new value is different to its existing
        // value, we set its value and get its downstream output gates.
        val outputs = inputs.flatMap { (gateName, newValue) ->
            val gate = inputGates[gateName] ?: throw IllegalArgumentException("Unknown input gate.")
            if (gate.value != newValue) {
                gate.value = newValue
                gate.outputs
            } else {
                listOf<Gate>()
            }
        }.toMutableList()

        // For each output, if its new value is different to its existing
        // value, we set its value and recursively update its downstream
        // output gates using the same approach.
        var i = 0
        while (i < outputs.size) {
            val output = outputs[i]
            val newValue = output.calculateNewValue()
            if (output.value != newValue) {
                output.value = newValue
                outputs.addAll(output.outputs)
            }
            i++
        }
    }

    fun getValue(gateName: String): Boolean {
        val outputGates = loadedChip?.outGates ?: throw IllegalStateException("No chip loaded in the simulator.")
        val outputGate = outputGates[gateName] ?: throw IllegalArgumentException("Unknown output gate.")
        return outputGate.value
    }

    private fun initialiseChip() {
        val inputGates = loadedChip?.inGates ?: throw IllegalStateException("No chip loaded in the simulator.")

        inputGates.values.forEach {
            gate -> gate.value = false
        }

        repeat(LOOPS_TO_INITIALISE) {
            val outputs = inputGates.values.flatMap { gate -> gate.outputs }.toMutableList()

            var i = 0
            while (i < outputs.size) {
                val output = outputs[i]
                val newValue = output.calculateNewValue()
                output.value = newValue
                output.outputs.forEach { newOutput ->
                    if (newOutput !in outputs) {
                        outputs.add(newOutput)
                    }
                }
                i++
            }
        }
    }
}