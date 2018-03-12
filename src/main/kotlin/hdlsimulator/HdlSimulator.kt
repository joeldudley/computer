package hdlsimulator

import hdlsimulator.generator.Generator
import hdlsimulator.parser.Parser
import hdlsimulator.tokeniser.Tokeniser
import java.io.File

// Maps input and output gate names to input and output gates for a chip.
data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

class HdlSimulator {
    // A function that returns a Nand gate.
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
    val chipGenerators = mutableMapOf(
            "Nand" to nandGenerator
    )
    // The chip currently being simulated.
    private var loadedChip: ChipIOGates? = null

    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()

    init {
        File("src/main/resources").listFiles().forEach { file ->
            val fileContents = file.readText()
            val tokens = tokeniser.tokenize(fileContents)
            parser.setInput(tokens)
            val chip = parser.parse()
            val chipFun = generator.generateChipFun(chip, chipGenerators)
            chipGenerators.put(chip.name, chipFun)
        }
    }

    fun loadChip(name: String) {
        loadedChip = chipGenerators[name]!!()
    }

    fun evaluateChip(inputs: List<Pair<String, Boolean>>) {
        val outputs = mutableListOf<Gate>()
        for ((gateName, newValue) in inputs) {
            val gate = loadedChip!!.inGates[gateName]!!
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

    fun readValue(gateName: String): Boolean{
        return loadedChip!!.outGates[gateName]!!.value
    }
}