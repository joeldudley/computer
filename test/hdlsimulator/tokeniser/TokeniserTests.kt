package hdlsimulator.tokeniser

import hdlsimulator.EXPECTED_AND_TOKENS
import hdlsimulator.EXPECTED_NOT_TOKENS
import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

class TokeniserTests {
    private val tokeniser = Tokeniser()

    @Test
    fun notTest() {
        val input = File("test/hdlsimulator/Not.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(EXPECTED_NOT_TOKENS, tokens)
    }

    @Test
    fun andTest() {
        val input = File("test/hdlsimulator/And.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(EXPECTED_AND_TOKENS, tokens)
    }
}