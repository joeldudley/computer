package hardwaresimulator.internal.evaluator

import hardwaresimulator.Chip
import hardwaresimulator.ChipInput
import hardwaresimulator.internal.Gate

private val LOOPS_TO_INITIALISE = 3

interface Evaluator {
    fun loadChip(chip: Chip)

    fun setInputs(inputValues: List<ChipInput>)

    fun getValue(gateName: String): Boolean
}