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

    private fun parseIns(): List<Node.Pin> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Expected token IN, got token ${tokens[pos]}.")
        pos++
        return parsePins("OUT")
    }

    private fun parseOuts(): List<Node.Pin> {
        if (tokens[pos] != "OUT") throw IllegalArgumentException("Expected token OUT, got token ${tokens[pos]}.")
        pos++
        return parsePins("PARTS:")
    }

    private fun parsePins(trailingWord: String): List<Node.Pin> {
        val ins = mutableListOf<Node.Pin>()
        // Using this check instead of `true` correctly handles the case of
        // no inputs.
        while (tokens[pos] != ";") {
            val inName = tokens[pos++]

            val inWidth = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                tokens[pos++].toInt()
                // Skip the closing square bracket.
                pos++
            } else {
                1
            }

            val input = Node.Pin(inName, inWidth)
            ins.add(input)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ";") break
            else if (tokens[pos] == trailingWord) throw IllegalArgumentException("Missing semi-colon after pins.")
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }

        pos++
        return ins
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
            val lhs = tokens[pos++]

            if (tokens[pos] != "=") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
            pos++

            val rhs = tokens[pos++]

            val assignment = Node.Assignment(lhs, rhs)
            assigments.add(assignment)

            val nextToken = tokens[pos]
            if (nextToken == ",") pos++
            else if (nextToken == ")") break
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after component name.")
        pos++

        return Node.Component(componentName, assigments)
    }
}