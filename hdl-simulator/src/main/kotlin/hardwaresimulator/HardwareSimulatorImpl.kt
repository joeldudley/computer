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

    override fun loadChipDefinitions(vararg chipDefinitionFolders: String) {
        val hdlFilesTokens = chipDefinitionFolders
                .flatMap { folder -> File(folder).listFiles().toList() }
                .filter { file -> file.extension == "hdl" }
                .map { file -> file.readText() }
                .map { text -> tokenizer.tokenize(text) }

        val sortedHdlFiles = sorter.orderChipDefinitions(hdlFilesTokens)

        // TODO: Easy way to map tokens to file they're from
        // TODO: Sorter to return sorted list of tokens
        // TODO: Cache library tokens in parser in order
    }

    override fun loadChip(name: String) {
        val chip = generator.generateChip(name)
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