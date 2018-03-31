package hardwaresimulator.internal.tokeniser

/**
 * Parses a string representing a .hdl chip definition into a list of tokens.
 */
internal class TokenizerImpl: Tokenizer {
    private var program = ""
    private var tokens = mutableListOf<String>()
    private var pos = 0
    private var inWord = false
    private var word = ""

    override fun tokenize(program: String): List<String> {
        this.program = program
        tokens = mutableListOf()
        pos = 0
        inWord = false
        word = ""

        while (pos < program.length) {
            when (program[pos]) {
                '/' -> processPossibleComment()
                ',', ';', '(', ')', '=', '[', ']' -> processNonWordCharacterToKeep()
                ' ', '\t', '\n', '\r' -> processNonWordCharacterToDiscard()
                else -> processWordCharacter()
            }
        }

        // If we're still in a word, add it to the list.
        if (inWord) tokens.add(word)

        return tokens
    }

    private fun processPossibleComment() {
        val nextChar = program.getOrNull(pos + 1)
        val charAfterNext = program.getOrNull(pos + 2)

        when {
            nextChar == '*' && charAfterNext == '*' -> processMultiLineComment()
            nextChar == '/' -> processOneLineComment()
            else -> { // Not a comment.
                inWord = true
                word += program[pos]
                pos++
            }
        }
    }

    private fun processMultiLineComment() {
        // We skip over the start of the comment.
        pos += 3
        while (true) {
            if (program.getOrNull(pos) == '*' && program.getOrNull(pos + 1) == '/') {
                pos += 2
                break
            } else {
                pos++
            }
        }
    }

    private fun processOneLineComment() {
        // We skip over the start of the comment.
        pos += 2
        while (program.getOrNull(pos) !in listOf('\n', '\r', null)) {
            pos++
        }
    }

    private fun processNonWordCharacterToKeep() {
        addCurrentWordToTokens()
        tokens.add(program[pos].toString())
        pos++
    }

    private fun processNonWordCharacterToDiscard() {
        addCurrentWordToTokens()
        pos++
    }

    private fun addCurrentWordToTokens() {
        if (inWord) {
            tokens.add(word)
            inWord = false
            word = ""
        }
    }

    private fun processWordCharacter() {
        inWord = true
        word += program[pos]
        pos++
    }
}