package hardwaresimulator

import hardwaresimulator.internal.Gate
import hardwaresimulator.internal.evaluator.Evaluator
import hardwaresimulator.internal.generator.Generator
import hardwaresimulator.internal.parser.Parser
import hardwaresimulator.internal.tokeniser.Tokeniser
import java.io.File

// Maps input and output gate names to input and output gates for a chip.
data class Chip(val inputs: Map<String, Gate>, val outputs: Map<String, Gate>)
data class ChipInput(val name: String, val value: Boolean)

/**
 * Loads chips into memory, simulates running them, and reads back the results.
 *
 * Can generate a Nand gate, plus any chips described in HDL format in the resources folder.
 */
class HardwareSimulatorImpl: HardwareSimulator {
    private val tokeniser = Tokeniser()
    private val parser = Parser()
    private val generator = Generator()
    private val evaluator = Evaluator()

    override fun loadChipDefinitions(vararg chipDefinitionFolders: String) {
        chipDefinitionFolders.forEach { folder ->
            val files = File(folder).listFiles()
            val hdlFiles = files.filter { file -> file.extension == "hdl" }
            hdlFiles.forEach { file ->
                val fileContents = file.readText()
                val tokens = tokeniser.tokenize(fileContents)
                val chip = parser.parse(tokens)
                generator.addChipDefinition(chip)
            }
        }
    }

    override fun loadChip(name: String) {
        val chip = generator.generateChip(name)
        evaluator.loadChip(chip)
    }

    override fun setInputs(vararg inputs: Pair<String, Boolean>) {
        val chipInputs = inputs.map { (name, value) -> ChipInput(name, value) }
        evaluator.setInputs(chipInputs)
    }

    override fun getValue(gateName: String): Boolean {
        return evaluator.getValue(gateName)
    }
}