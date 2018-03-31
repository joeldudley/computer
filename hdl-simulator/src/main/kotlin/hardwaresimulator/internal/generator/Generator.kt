package hardwaresimulator.internal.generator

import hardwaresimulator.Chip
import hardwaresimulator.internal.ChipNode

interface Generator {
    fun generateChip(chipNode: ChipNode): Chip
}