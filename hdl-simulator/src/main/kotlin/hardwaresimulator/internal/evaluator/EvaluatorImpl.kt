package hardwaresimulator.internal.evaluator

import hardwaresimulator.Chip
import hardwaresimulator.ChipInput
import hardwaresimulator.internal.Gate

private val LOOPS_TO_INITIALISE = 3

internal class EvaluatorImpl : Evaluator {
    // The chip currently being simulated.
    private var loadedChip: Chip? = null

    override fun loadChip(chip: Chip) {
        loadedChip = chip
        initialiseChip()
    }

    override fun setInput(name: String, values: List<Boolean>) {
        val inputGateMap = loadedChip?.inputGateMap ?: throw IllegalStateException("No chip loaded in the simulator.")

        // For each input gate, if its new value is different to its existing
        // value, we set its value and get its downstream output gates.
        val gatesToUpdate = mutableListOf<Gate>()

        val inputPins = inputGateMap[name] ?: throw IllegalArgumentException("Unknown input gate.")

        for ((pin, value) in inputPins.zip(values)) {
            if (pin.value != value) {
                pin.value = value
                gatesToUpdate.addAll(pin.outputs)
            }
        }

        // For each output, if its new value is different to its existing
        // value, we set its value and recursively update its downstream
        // output gates using the same approach.
        var i = 0
        while (i < gatesToUpdate.size) {
            val gateToUpdate = gatesToUpdate[i]
            val newValue = gateToUpdate.calculateNewValue()
            if (gateToUpdate.value != newValue) {
                gateToUpdate.value = newValue
                gatesToUpdate.addAll(gateToUpdate.outputs)
            }
            i++
        }
    }

    override fun getValue(gateName: String): List<Boolean> {
        val outputGateMap = loadedChip?.outputGateMap ?: throw IllegalStateException("No chip loaded in the simulator.")
        val outputGate = outputGateMap[gateName] ?: throw IllegalArgumentException("Unknown output gate.")
        return outputGate.map {
            it.value
        }
    }

    private fun initialiseChip() {
        val inputGateMap = loadedChip?.inputGateMap ?: throw IllegalStateException("No chip loaded in the simulator.")
        val allInputPins = inputGateMap.values.flatten()
        allInputPins.forEach { pin ->
            pin.value = false
        }

        repeat(LOOPS_TO_INITIALISE) {
            val pinsToUpdate = mutableListOf<Gate>()
            allInputPins.forEach { pin ->
                pinsToUpdate.addAll(pin.outputs)
            }

            var i = 0
            while (i < pinsToUpdate.size) {
                val gateToUpdate = pinsToUpdate[i]
                val newValue = gateToUpdate.calculateNewValue()
                gateToUpdate.value = newValue
                gateToUpdate.outputs.forEach { newOutput ->
                    if (newOutput !in pinsToUpdate) {
                        pinsToUpdate.add(newOutput)
                    }
                }
                i++
            }
        }
    }
}