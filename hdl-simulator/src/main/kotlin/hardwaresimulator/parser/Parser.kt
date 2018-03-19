package hardwaresimulator.parser

internal class Parser {
    private var pos = 0
    private var tokens = listOf<String>()

    fun setInput(tokens: List<String>) {
        pos = 0
        this.tokens = tokens
    }

    fun parse(): Node.Chip {
        val chipName = parseChipName()
        val ins = parseIns()
        val outs = parseOuts()
        val components = parseParts()
        return Node.Chip(chipName, ins, outs, components)
    }

    private fun parseChipName(): String {
        if (tokens[pos] != "CHIP") throw IllegalArgumentException("Expected token CHIP, got token ${tokens[pos]}.")
        pos++
        val chipName = tokens[pos++]
        if (tokens[pos] != "{") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        return chipName
    }

    private fun parseIns(): List<Node.IOPin> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Expected token IN, got token ${tokens[pos]}.")
        pos++
        return parsePins("OUT")
    }

    private fun parseOuts(): List<Node.IOPin> {
        if (tokens[pos] != "OUT") throw IllegalArgumentException("Expected token OUT, got token ${tokens[pos]}.")
        pos++
        return parsePins("PARTS:")
    }

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

            val nextToken = tokens[pos]
            if (nextToken == ",") pos++
            else if (nextToken == ";") break
            else if (nextToken == trailingWord) throw IllegalArgumentException("Missing semi-colon after pins.")
            else throw IllegalArgumentException("Malformed input: $nextToken.")
        }

        pos++
        return pins
    }

    private fun parseParts(): List<Node.Component> {
        if (tokens[pos] != "PARTS:") throw IllegalArgumentException("Expected token PARTS:, got token ${tokens[pos]}.")
        pos++

        val components = mutableListOf<Node.Component>()
        while (pos < tokens.size) {
            if (tokens[pos] == "}") {
                break
            } else {
                val component = parseComponent()
                components.add(component)
            }
        }

        return components
    }

    private fun parseComponent(): Node.Component {
        val componentName = tokens[pos++]
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

            val nextToken = tokens[pos]
            if (nextToken == ",") pos++
            else if (nextToken == ")") break
            else throw IllegalArgumentException("Malformed input: $nextToken.")
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after component name.")
        pos++

        return Node.Component(componentName, assigments)
    }
}