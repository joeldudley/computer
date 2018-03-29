package hardwaresimulator.internal.evaluator

import hardwaresimulator.Chip
import hardwaresimulator.ChipInput
import hardwaresimulator.internal.Gate

val LOOPS_TO_INITIALISE = 3

class Evaluator {
    // The chip currently being simulated.
    private var loadedChip: Chip? = null

    fun loadChip(chip: Chip) {
        loadedChip = chip
        initialiseChip()
    }

    fun setInputs(inputs: List<ChipInput>) {
        val inputGates = loadedChip?.inputs ?: throw IllegalStateException("No chip loaded in the simulator.")

        // For each input gate, if its new value is different to its existing
        // value, we set its value and get its downstream output gates.
        val outputs = mutableListOf<Gate>()

        inputs.forEach { input ->
            val gate = inputGates[input.name] ?: throw IllegalArgumentException("Unknown input gate.")
            if (gate.value != input.value) {
                gate.value = input.value
                outputs.addAll(gate.outputs)
            }
        }

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
        val outputGates = loadedChip?.outputs ?: throw IllegalStateException("No chip loaded in the simulator.")
        val outputGate = outputGates[gateName] ?: throw IllegalArgumentException("Unknown output gate.")
        return outputGate.value
    }

    private fun initialiseChip() {
        val inputGates = loadedChip?.inputs ?: throw IllegalStateException("No chip loaded in the simulator.")

        inputGates.values.forEach { gate ->
            gate.value = false
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