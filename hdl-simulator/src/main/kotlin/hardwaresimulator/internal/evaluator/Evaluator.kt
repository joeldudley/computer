package hardwaresimulator.internal.evaluator

import hardwaresimulator.Chip
import hardwaresimulator.ChipInput
import hardwaresimulator.internal.Gate

interface Evaluator {
    fun loadChip(chip: Chip)

    fun setInput(name: String, values: List<Boolean>)

    fun getValue(gateName: String): List<Boolean>
}