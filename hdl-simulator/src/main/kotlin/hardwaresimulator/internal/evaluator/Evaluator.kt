package hardwaresimulator.internal.evaluator

import hardwaresimulator.Chip
import hardwaresimulator.ChipInput

interface Evaluator {
    fun loadChip(chip: Chip)

    fun setInputs(inputValues: List<ChipInput>)

    fun getValue(gateName: String): List<Boolean>
}