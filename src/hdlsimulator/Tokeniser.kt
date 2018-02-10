package hdlsimulator

internal class Tokeniser {
    fun tokenize(program: String): List<String> {
        val tokens = mutableListOf<String>()
        var pos = 0
        var inWord = false
        var word = ""

        while (pos < program.length) {
            val char = program[pos]
            when (char) {
                '/' -> { // Possibly a comment.
                    val nextChar = program.getOrNull(pos + 1)
                    val charAfterNext = program.getOrNull(pos + 2)

                    when {
                        nextChar == '*' && charAfterNext == '*' -> { // A multi-line comment.
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
                        nextChar == '/' -> { // A one-line comment.
                            // We skip over the start of the comment.
                            pos += 2
                            while (program.getOrNull(pos) !in listOf('\n', '\r', null)) {
                                pos++
                            }
                        }
                        else -> { // Not a comment.
                            inWord = true
                            word += program[pos++]
                        }
                    }
                }

                '(', ')', '=' -> {
                    if (inWord) {
                        tokens.add(word)
                        inWord = false
                        word = ""
                    }
                    tokens.add(char.toString())
                    pos++
                }

                ',', ';', ' ', '\t', '\n', '\r' -> { // Non-word character.
                    if (inWord) {
                        tokens.add(word)
                        inWord = false
                        word = ""
                    }
                    pos++
                }

                else -> { // Word character.
                    inWord = true
                    word += program[pos++]
                }
            }
        }

        if (inWord) tokens.add(word)

        return tokens.toList()
    }
}