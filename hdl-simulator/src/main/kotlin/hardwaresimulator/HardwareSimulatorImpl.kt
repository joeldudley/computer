package hardwaresimulator

import hardwaresimulator.internal.Gate
import hardwaresimulator.internal.evaluator.Evaluator
import hardwaresimulator.internal.generator.Generator
import hardwaresimulator.internal.parser.Parser
import hardwaresimulator.internal.sorter.Sorter
import hardwaresimulator.internal.tokeniser.Tokenizer
import java.io.File

// Maps input and output gate names to input and output gates for a chip.
data class Chip(val inputGateMap: Map<String, Gate>, val outputGateMap: Map<String, Gate>)

data class ChipInput(val name: String, val value: Boolean)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HardwareSimulatorImpl : HardwareSimulator {
    private val tokenizer = Tokenizer()
    private val sorter = Sorter()
    private val parser = Parser()
    private val generator = Generator()
    private val evaluator = Evaluator()

    override fun loadLibraryChips(vararg paths: String) {
        // TODO: We assume here the paths are folders. What if they're files? Handle that.
        val files = paths.flatMap { folder -> File(folder).listFiles().toList() }
        val hdlFiles = files.filter { file -> file.extension == "hdl" }
        val hdlFileTexts = hdlFiles.map { file -> file.readText() }
        val hdlTokens = hdlFileTexts.map { text -> tokenizer.tokenize(text) }
        val sortedHdlTokens = sorter.orderChipDefinitions(hdlTokens)
        for (tokens in sortedHdlTokens) {
            parser.parseAndCacheLibraryPart(tokens)
        }
    }

    override fun loadChip(path: String) {
        val file = File(path)
        if (file.extension != "hdl") {
            throw IllegalArgumentException("Wrong file extension. Expected .hdl but got .${file.extension}.")
        }
        val text = file.readText()
        val tokens = tokenizer.tokenize(text)
        val node = parser.parse(tokens)
        val chip = generator.generateChip(node)
        evaluator.loadChip(chip)
    }

    override fun setInputs(vararg inputValues: Pair<String, Boolean>) {
        val chipInputs = inputValues.map { (name, value) -> ChipInput(name, value) }
        evaluator.setInputs(chipInputs)
    }

    override fun getValue(gateName: String): Boolean {
        return evaluator.getValue(gateName)
    }
}