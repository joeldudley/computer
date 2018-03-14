package hdlsimulator

import hdlsimulator.generator.Generator
import hdlsimulator.parser.Parser
import hdlsimulator.tokeniser.Tokeniser
import java.io.File

// Maps input and output gate names to input and output gates for a chip.
data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HdlSimulator(chipDefFolders: List<String>) {
    // A function that generates a Nand gate.
    private val nandGenerator = fun(): ChipIOGates {
        val nandGate = NandGate()

        val in1 = PassthroughGate()
        nandGate.in1 = in1
        in1.outputs.add(nandGate)

        val in2 = PassthroughGate()
        nandGate.in2 = in2
        in2.outputs.add(nandGate)

        return ChipIOGates(
                inGates = mapOf("a" to in1, "b" to in2),
                outGates = mapOf("out" to nandGate))
    }

    // Known chip generators.
    private val chipGenerators = mutableMapOf(
            "Nand" to nandGenerator
    )
    // The chip currently being simulated.
    private lateinit var loadedChip: ChipIOGates

    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()

    init {
        chipDefFolders.forEach { folder ->
            File(folder).listFiles().forEach {
                file -> addChipGenerator(file)
            }
        }
    }

    private fun addChipGenerator(file: File) {
        val fileContents = file.readText()
        val tokens = tokeniser.tokenize(fileContents)
        parser.setInput(tokens)
        val chip = parser.parse()
        val chipFun = generator.generateChipFun(chip, chipGenerators)
        chipGenerators.put(chip.name, chipFun)
    }

    fun loadChip(name: String) {
        loadedChip = chipGenerators[name]?.invoke() ?: throw IllegalArgumentException("Unknown chip.")
    }

    fun evaluateChip(inputs: List<Pair<String, Boolean>>) {
        // We set the value of each input gate and get the downstream output gates.
        val outputs = inputs.flatMap { (gateName, newValue) ->
            val gate = loadedChip.inGates[gateName] ?: throw IllegalArgumentException("Unknown input gate.")
            gate.value = newValue
            gate.outputs
        }.toMutableList()

        // We recursively update the downstream outputs.
        var i = 0
        while (i < outputs.size) {
            val output = outputs[i]
            output.update()
            outputs.addAll(output.outputs)
            i++
        }
    }

    fun readValue(gateName: String): Boolean {
        return loadedChip.outGates[gateName]?.value ?: throw IllegalArgumentException("Unknown output gate.")
    }
}