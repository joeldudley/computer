package hdlsimulator

import hdlsimulator.generator.Generator
import hdlsimulator.parser.Parser
import hdlsimulator.tokeniser.Tokeniser
import java.io.File

data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

val nandGateFun = fun(): ChipIOGates {
    val nandGate = NandGate()

    val input = Gate()
    nandGate.in1 = input
    input.outputs.add(nandGate)

    val auxInput = Gate()
    nandGate.in2 = auxInput
    auxInput.outputs.add(nandGate)

    val ins = mapOf("a" to input, "b" to auxInput)
    val outs = mapOf("out" to nandGate)
    return ChipIOGates(ins, outs)
}

class HdlSimulator {
    val chipFuns = mutableMapOf<String, () -> ChipIOGates>()
    private var loadedChip: ChipIOGates? = null

    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()

    init {
        chipFuns.put("Nand", nandGateFun)

        for (file in File("./resources").listFiles()) {
            val fileContents = file.readText()
            val tokens = tokeniser.tokenize(fileContents)
            parser.setInput(tokens)
            val chip = parser.parse()
            val chipFun = generator.generateChipFun(chip, chipFuns)
            chipFuns.put(chip.name, chipFun)
        }
    }

    fun loadChip(name: String) {
        loadedChip = chipFuns[name]!!()
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