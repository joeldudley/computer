package hardwaresimulator.internal.parser

import hardwaresimulator.internal.*

/**
 * Parses a list of tokens into a [Node.Chip].
 */
internal class Parser {
    // The current position in the list of tokens.
    private var pos = 0
    // The list of tokens to parse.
    private var tokens = listOf<String>()

    /**
     * Parses the [tokens] into a [Node.Chip].
     */
    fun parse(tokens: List<String>): ChipNode {
        pos = 0
        this.tokens = tokens
        return ChipNode(parseChipName(), parseInputs(), parseOutputs(), parseParts())
    }

    /**
     * Parses the chip's header, in the form "CHIP <Name> {".
     *
     * @return The name of the chip.
     */
    private fun parseChipName(): String {
        if (tokens[pos] != "CHIP") throw IllegalArgumentException("Expected token CHIP, got token ${tokens[pos]}.")
        pos++
        val chipName = tokens[pos]
        pos++
        if (tokens[pos] != "{") throw IllegalArgumentException("Expected token {, got token ${tokens[pos]}.")
        pos++
        return chipName
    }

    /**
     * Parses the chip's inputs, in the form "IN <In1>, ..., <InN>;".
     *
     * @return The inputs.
     */
    private fun parseInputs(): List<IOPinNode> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Expected token IN, got token ${tokens[pos]}.")
        pos++
        return parsePins("OUT")
    }

    /**
     * Parses the chip's inputs, in the form "IN <In1>, ..., <InN>;".
     *
     * @return The inputs.
     */
    private fun parseOutputs(): List<IOPinNode> {
        if (tokens[pos] != "OUT") throw IllegalArgumentException("Expected token OUT, got token ${tokens[pos]}.")
        pos++
        return parsePins("PARTS:")
    }

    /**
     * Parses the chip's input and output pin lists, in the form "<In1>, ..., <InN>;".
     *
     * @param trailingToken The token which indicates the final semi-colon was
     *   missing and the parser has overrun.
     * @return The pins.
     */
    private fun parsePins(trailingToken: String): List<IOPinNode> {
        val pins = mutableListOf<IOPinNode>()
        // Using this check instead of `true` correctly handles the case of
        // no pins.
        while (tokens[pos] != ";") {
            val pinName = tokens[pos]
            pos++

            val pinWidth = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                val width = tokens[pos].toInt()
                pos++
                // Skip the closing square bracket.
                pos++
                width
            } else {
                1
            }

            val pin = IOPinNode(pinName, pinWidth)
            pins.add(pin)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ";") break
            else if (tokens[pos] == trailingToken) throw IllegalArgumentException("Missing semi-colon after pins.")
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }

        pos++
        return pins
    }

    /**
     * Parses the chip's parts, in the form "PARTS <Part1>, ..., <PartN> }",
     * where each part has the form "<PartName>(<Lhs1>=<Rhs1>, ..., <LhsN>=<RhsN>);".
     *
     * @return The parts.
     */
    private fun parseParts(): List<PartNode> {
        if (tokens[pos] != "PARTS:") throw IllegalArgumentException("Expected token PARTS:, got token ${tokens[pos]}.")
        pos++

        val parts = mutableListOf<PartNode>()
        while (tokens[pos] != "}") {
            val part = parsePart()
            parts.add(part)
        }

        return parts
    }

    /**
     * Parses an individual part, in the form "<PartName>(<Lhs1>=<Rhs1>, ..., <LhsN>=<RhsN>);".
     *
     * @return The part.
     */
    private fun parsePart(): PartNode {
        val partName = tokens[pos]
        pos++
        if (tokens[pos] != "(") throw IllegalArgumentException("Expected token (, got token ${tokens[pos]}.")
        pos++

        val assignments = mutableListOf<AssignmentNode>()
        while (true) {
            val lhs = InternalPinNode(tokens[pos], 0)
            pos++

            if (tokens[pos] != "=") throw IllegalArgumentException("Expected token =, got token ${tokens[pos]}.")
            pos++

            val rhsName = tokens[pos]
            pos++

            val rhsIndex = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                val index = tokens[pos].toInt()
                pos++
                // Skip the closing square bracket.
                pos++
                index
            } else {
                0
            }

            val rhs = InternalPinNode(rhsName, rhsIndex)

            val assignment = AssignmentNode(lhs, rhs)
            assignments.add(assignment)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ")") break
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after part name.")
        pos++

        return PartNode(partName, assignments)
    }
}