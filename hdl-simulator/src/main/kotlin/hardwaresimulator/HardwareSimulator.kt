package hardwaresimulator

import hardwaresimulator.evaluator.Evaluator
import hardwaresimulator.generator.Generator
import hardwaresimulator.parser.Parser
import hardwaresimulator.tokeniser.Tokeniser
import java.io.File

val LOOPS_TO_INITIALISE = 3

// Maps input and output gate names to input and output gates for a chip.
data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HardwareSimulator(chipDefFolders: List<String>) {
    // The chip currently being simulated.
    private var loadedChip: ChipIOGates? = null

    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()
    private val evaluator = Evaluator()

    init {
        chipDefFolders.forEach { folder ->
            val files = File(folder).listFiles()
            files.forEach { file ->
                val fileContents = file.readText()
                val tokens = tokeniser.tokenize(fileContents)
                val chip = parser.parse(tokens)
                generator.addChipDefinition(chip)
            }
        }
    }

    fun loadChip(name: String) {
        loadedChip = generator.generateChip(name)
        initialiseChip()
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
}