package hdlsimulator.parser

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

    private fun parseIns(): List<String> {
        if (tokens[pos] != "IN") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        val ins = mutableListOf<String>()
        while (tokens[pos] != ";") {
            // TODO: Handle missing semicolon.
            val inName = tokens[pos]
            ins.add(inName)
            pos++
        }
        pos++
        return ins
    }

    private fun parseOuts(): List<String> {
        if (tokens[pos] != "OUT") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
        pos++
        val ins = mutableListOf<String>()
        while (tokens[pos] != ";") {
            // TODO: Handle missing semicolon.
            val inName = tokens[pos]
            ins.add(inName)
            pos++
        }
        pos++
        return ins
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
        while (tokens[pos] != ")") {
            val lhs = tokens[pos]
            pos++
            if (tokens[pos] != "=") throw IllegalArgumentException("Malformed input: ${tokens[pos]}.")
            pos++
            val rhs = tokens[pos]
            pos++
            val assignment = Node.Assignment(lhs, rhs)
            assigments.add(assignment)
        }
        pos++
        if (tokens[pos] != ";") throw IllegalArgumentException("Missing semi-colon after component name.")
        pos++

        return Node.Component(componentName, assigments)
    }
}