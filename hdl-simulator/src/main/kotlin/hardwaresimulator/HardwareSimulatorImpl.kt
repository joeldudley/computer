package hardwaresimulator

import com.sun.org.apache.xpath.internal.operations.Bool
import hardwaresimulator.internal.Gate
import hardwaresimulator.internal.evaluator.EvaluatorImpl
import hardwaresimulator.internal.generator.GeneratorImpl
import hardwaresimulator.internal.parser.ParserImpl
import hardwaresimulator.internal.sorter.SorterImpl
import hardwaresimulator.internal.tokeniser.TokenizerImpl
import java.io.File
import java.util.AbstractMap.SimpleImmutableEntry

// TODO: These should be internal.
// Maps input and output gate names to input and output gates for a chip.
data class Chip(val inputGateMap: Map<String, List<Gate>>, val outputGateMap: Map<String, List<Gate>>)
data class ChipInput(val name: String, val values: List<Boolean>)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HardwareSimulatorImpl: HardwareSimulator {
    private val tokenizer = TokenizerImpl()
    private val sorter = SorterImpl()
    private val parser = ParserImpl()
    private val generator = GeneratorImpl()
    private val evaluator = EvaluatorImpl()

    override fun loadChipDefinitions(path: String) {
        loadChipDefinitions(listOf(path))
    }

    override fun loadChipDefinitions(paths: List<String>) {
        val files = pathsToFiles(paths.toList())
        val hdlFiles = files.filter { file -> file.extension == "hdl" }
        val hdlTokens = hdlFiles.map { file -> tokenizer.tokenize(file.readText()) }
        val orderedHdlTokens = sorter.topologicallySortChipDefinitions(hdlTokens)
        orderedHdlTokens.forEach { tokens -> parser.parseAndCache(tokens) }
    }

    override fun loadChip(name: String) {
        val node = parser.retrieve(name)
        val chip = generator.generateChip(node)
        evaluator.loadChip(chip)
    }

    override fun setInput(name: String, values: List<Boolean>) {
        evaluator.setInput(name, values)
    }

    override fun getOutput(gateName: String): List<Boolean> {
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