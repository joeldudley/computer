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
        parseParts()

        // TODO: Refactor this into parseParts
        val components = mutableListOf<Node.Component>()
        while (pos < tokens.size) {
            if (tokens[pos] == "}") {
                break
            } else {
                val component = parseComponent()
                components.add(component)
            }
        }

        return Node.Chip(chipName, ins, outs, components)
    }

    private fun parseChipName(): String {
        if (tokens[pos] != "CHIP") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        val chipName = tokens[pos]
        pos++
        if (tokens[pos] != "{") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        return chipName
    }

    private fun parseIns(): List<Node.Input> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        val ins = mutableListOf<Node.Input>()
        while (true) {
            val inName = tokens[pos]
            pos++

            val inWidth = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                tokens[pos].toInt()
                pos++
                // Skip the closing square bracket.
                pos++
            } else {
                1
            }

            val input = Node.Input(inName, inWidth)
            ins.add(input)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ";") break
            else if (tokens[pos] == "OUT") throw IllegalArgumentException("Missing semi-colon after inputs.")
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }

        pos++
        return ins
    }

    private fun parseOuts(): List<Node.Output> {
        pos++
        val outs = mutableListOf<Node.Output>()
        while (true) {
            val outName = tokens[pos]
            pos++

            val outWidth = if (tokens[pos] == "[") {
                // Skip the opening square bracket.
                pos++
                tokens[pos].toInt()
                pos++
                // Skip the closing square bracket.
                pos++
            } else {
                1
            }

            val output = Node.Output(outName, outWidth)
            outs.add(output)

            if (tokens[pos] == ",") pos++
            else if (tokens[pos] == ";") break
            else if (tokens[pos] == "PARTS:") throw IllegalArgumentException("Missing semi-colon after outputs.")
            else throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        }

        pos++
        return outs
    }

    private fun parseParts() {
        if (tokens[pos] != "PARTS:") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
    }

    private fun parseComponent(): Node.Component {
        val componentName = tokens[pos]
        pos++
        if (tokens[pos] != "(") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++

        val assigments = mutableListOf<Node.Assignment>()
        while (true) {
            val lhs = tokens[pos]
            pos++

            if (tokens[pos] != "=") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
            pos++

            val rhs = tokens[pos]
            pos++

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