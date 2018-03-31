package hardwaresimulator.internal.parser

import hardwaresimulator.internal.ChipNode

/**
 * Parses a list of tokens into a [ChipNode].
 */
interface Parser {
    /**
     * Parses the [tokens] and caches the resulting [ChipNode].
     */
    fun parseAndCache(tokens: List<String>)

    fun retrieve(name: String): ChipNode
}