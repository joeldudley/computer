package hdlsimulator.tokeniser

import hdlsimulator.EXPECTED_AND_TOKENS
import hdlsimulator.EXPECTED_NOT_TOKENS
import hdlsimulator.EXPECTED_OR_TOKENS
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

class TokeniserTests {
    private val tokeniser = Tokeniser()

    @Test
    fun notTest() {
        val input = File("test/hdlsimulator/tokeniser/Not.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(EXPECTED_NOT_TOKENS, tokens)
    }

    @Test
    fun andTest() {
        val input = File("test/hdlsimulator/tokeniser/And.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(EXPECTED_AND_TOKENS, tokens)
    }

    @Test
    fun orTest() {
        val input = File("test/hdlsimulator/tokeniser/Or.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(EXPECTED_OR_TOKENS, tokens)
    }
}