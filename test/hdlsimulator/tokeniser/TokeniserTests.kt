package hdlsimulator.tokeniser

import hdlsimulator.AND_HDL_TOKENS
import hdlsimulator.NOT_HDL_TOKENS
import hdlsimulator.OR_HDL_TOKENS
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

class TokeniserTests {
    private val tokeniser = Tokeniser()

    @Test
    fun notTest() {
        val input = File("test/hdlsimulator/tokeniser/Not.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(NOT_HDL_TOKENS, tokens)
    }

    @Test
    fun andTest() {
        val input = File("test/hdlsimulator/tokeniser/And.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(AND_HDL_TOKENS, tokens)
    }

    @Test
    fun orTest() {
        val input = File("test/hdlsimulator/tokeniser/Or.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(OR_HDL_TOKENS, tokens)
    }
}