package hardwaresimulator.internal.parser

import hardwaresimulator.internal.ChipNode
import hardwaresimulator.internal.Node

/**
 * Parses a list of tokens into a [Node.Chip].
 */
interface Parser {
    /**
     * Parses the [tokens] into a [Node.Chip].
     */
    fun parse(tokens: List<String>): ChipNode

    fun parseAndCacheLibraryPart(tokens: List<String>)
}