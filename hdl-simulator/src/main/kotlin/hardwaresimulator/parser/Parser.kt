package hardwaresimulator.parser

internal class Parser {
    private var pos = 0
    private var tokens = listOf<String>()

    /**
     * Parses the [tokens] into a [Node.Chip].
     */
    fun parse(tokens: List<String>): Node.Chip {
        pos = 0
        this.tokens = tokens

        val chipName = parseChipName()
        val ins = parseInputs()
        val outs = parseOutputs()
        val parts = parseParts()
        return Node.Chip(chipName, ins, outs, parts)
    }

    /**
     * Parses the chip's header, in the form "CHIP <Name> {".
     *
     * @return The name of the chip.
     */
    private fun parseChipName(): String {
        if (tokens[pos] != "CHIP") throw IllegalArgumentException("Expected token CHIP, got token ${tokens[pos]}.")
        pos++
        val chipName = tokens[pos++]
        if (tokens[pos] != "{") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
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
     * Parses the chip's pins, in the form "PARTS <Part1>, ..., <PartN> }",
     * where each part has the form "<CompName>(<Lhs1>=<Rhs1>, ..., <LhsN>=<RhsN>);".
     *
     * @return The parts.
     */
    private fun parsePins(trailingWord: String): List<Node.IOPin> {
        val pins = mutableListOf<Node.IOPin>()
        // Using this check instead of `true` correctly handles the case of
        // no inputs.
        while (tokens[pos] != ";") {
            val pinName = tokens[pos++]

            val pinWidth = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                val width = tokens[pos++].toInt()
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
            else if (tokens[pos] == trailingWord) throw IllegalArgumentException("Missing semi-colon after pins.")
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
        while (pos < tokens.size) {
            if (tokens[pos] == "}") {
                break
            } else {
                val part = parsePart()
                parts.add(part)
            }
        }

        return parts
    }

    private fun parsePart(): Node.Part {
        val partName = tokens[pos++]
        if (tokens[pos] != "(") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++

        val assigments = mutableListOf<Node.Assignment>()
        while (true) {
            val lhs = Node.Pin(tokens[pos++], 0)

            if (tokens[pos] != "=") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
            pos++

            val rhsName = tokens[pos++]

            val rhsIndex = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                val index = tokens[pos++].toInt()
                // Skip the closing square bracket.
                pos++
                index
            } else {
                0
            }

            val rhs = Node.Pin(rhsName, rhsIndex)

            val assignment = Node.Assignment(lhs, rhs)
            assigments.add(assignment)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ")") break
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after part name.")
        pos++

        return Node.Part(partName, assigments)
    }
}