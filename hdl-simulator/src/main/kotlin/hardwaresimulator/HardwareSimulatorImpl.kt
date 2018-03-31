package hardwaresimulator

import hardwaresimulator.internal.Gate
import hardwaresimulator.internal.evaluator.EvaluatorImpl
import hardwaresimulator.internal.generator.GeneratorImpl
import hardwaresimulator.internal.parser.ParserImpl
import hardwaresimulator.internal.sorter.SorterImpl
import hardwaresimulator.internal.tokeniser.TokenizerImpl
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
    private val tokenizer = TokenizerImpl()
    private val sorter = SorterImpl()
    private val parser = ParserImpl()
    private val generator = GeneratorImpl()
    private val evaluator = EvaluatorImpl()

    override fun loadLibraryChips(vararg paths: String) {
        val files = pathsToFiles(paths.toList())
        val hdlFiles = files.filter { file -> file.extension == "hdl" }
        val hdlTokens = hdlFiles.map { file -> tokenizer.tokenize(file.readText()) }
        val orderedHdlTokens = sorter.topologicallySortChipDefinitions(hdlTokens)
        orderedHdlTokens.forEach { tokens -> parser.parse(tokens) }
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

    override fun getOutput(gateName: String): Boolean {
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