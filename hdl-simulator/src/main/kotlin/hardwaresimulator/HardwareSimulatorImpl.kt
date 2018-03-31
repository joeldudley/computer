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
        val files = pathsToFiles(paths.toList())
        val hdlFiles = files.filter { file -> file.extension == "hdl" }
        val hdlTokens = hdlFiles.map { file -> tokenizer.tokenize(file.readText()) }
        val sortedHdlTokens = sorter.orderChipDefinitions(hdlTokens)
        sortedHdlTokens.forEach { tokens -> parser.parseAndCacheLibraryPart(tokens) }
    }

    override fun loadChip(path: String) {
        val file = File(path)
        if (file.extension != "hdl") {
            throw IllegalArgumentException("Wrong file extension. Expected .hdl but got .${file.extension}.")
        }
        val tokens = tokenizer.tokenize(file.readText())
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

    private fun pathsToFiles(paths: List<String>): List<File> {
        return paths.flatMap { path ->
            val fileOrDirectory = File(path)
            if (fileOrDirectory.isDirectory) {
                fileOrDirectory.listFiles().toList()
            } else {
                listOf(fileOrDirectory)
            }
        }
    }
}