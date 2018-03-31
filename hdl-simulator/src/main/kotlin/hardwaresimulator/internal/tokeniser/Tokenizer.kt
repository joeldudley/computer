package hardwaresimulator.internal.tokeniser

/**
 * Parses a string representing a .hdl chip definition into a list of tokens.
 */
internal interface Tokenizer {
    fun tokenize(program: String): List<String>
}