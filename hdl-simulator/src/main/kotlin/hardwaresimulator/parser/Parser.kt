package hardwaresimulator.parser

/**
 * Parses a list of tokens into a [Node.Chip].
 */
internal class Parser {
    // The current position in the list of tokens.
    private var pos: Int = 0
    // The list of tokens to parse.
    private lateinit var tokens: List<String>

    /**
     * Parses the [tokens] into a [Node.Chip].
     */
    fun parse(tokens: List<String>): Node.Chip {
        pos = 0
        this.tokens = tokens
        return Node.Chip(parseChipName(), parseInputs(), parseOutputs(), parseParts())
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
    private fun parseInputs(): List<Node.IOPin> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Expected token IN, got token ${tokens[pos]}.")
        pos++
        return parsePins("OUT")
    }

    /**
     * Parses the chip's inputs, in the form "IN <In1>, ..., <InN>;".
     *
     * @return The inputs.
     */
    private fun parseOutputs(): List<Node.IOPin> {
        if (tokens[pos] != "OUT") throw IllegalArgumentException("Expected token OUT, got token ${tokens[pos]}.")
        pos++
        return parsePins("PARTS:")
    }

    /**
     * Parses the chip's input and output pin lists, in the form "<In1>, ..., <InN>;".
     *
     * @param trailingWord The token which indicates the final semi-colon was
     *   missing and the parser has overrun.
     * @return The pins.
     */
    private fun parsePins(trailingToken: String): List<Node.IOPin> {
        val pins = mutableListOf<Node.IOPin>()
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

            val pin = Node.IOPin(pinName, pinWidth)
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
    private fun parseParts(): List<Node.Part> {
        if (tokens[pos] != "PARTS:") throw IllegalArgumentException("Expected token PARTS:, got token ${tokens[pos]}.")
        pos++

        val parts = mutableListOf<Node.Part>()
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
    private fun parsePart(): Node.Part {
        val partName = tokens[pos]
        pos++
        if (tokens[pos] != "(") throw IllegalArgumentException("Expected token (, got token ${tokens[pos]}.")
        pos++

        val assignments = mutableListOf<Node.Assignment>()
        while (true) {
            val lhs = Node.Pin(tokens[pos], 0)
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

            val rhs = Node.Pin(rhsName, rhsIndex)

            val assignment = Node.Assignment(lhs, rhs)
            assignments.add(assignment)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ")") break
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after part name.")
        pos++

        return Node.Part(partName, assignments)
    }
}