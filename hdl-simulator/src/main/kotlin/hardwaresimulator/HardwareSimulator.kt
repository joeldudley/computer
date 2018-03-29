package hardwaresimulator

import hardwaresimulator.evaluator.Evaluator
import hardwaresimulator.generator.Generator
import hardwaresimulator.parser.Parser
import hardwaresimulator.tokeniser.Tokeniser
import java.io.File

// Maps input and output gate names to input and output gates for a chip.
data class ChipIOGates(val inGates: Map<String, Gate>, val outGates: Map<String, Gate>)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HardwareSimulator(chipDefinitionFolders: List<String>) {
    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()
    private val evaluator = Evaluator()

    init {
        chipDefinitionFolders.forEach { folder ->
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
        val chip = generator.generateChip(name)
        evaluator.loadChip(chip)
    }

    fun setInputs(inputs: List<Pair<String, Boolean>>) {
        evaluator.setInputs(inputs)
    }

    fun getValue(gateName: String): Boolean {
        return evaluator.getValue(gateName)
    }
}