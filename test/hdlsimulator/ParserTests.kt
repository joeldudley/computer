package hdlsimulator

import org.testng.annotations.Test
import java.io.File
import kotlin.test.assertEquals

class ParserTests {
    private val tokeniser = Tokeniser()

    @Test
    fun notTest() {
        val input = File("test/hdlsimulator/Not.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        val expectedTokens = listOf("CHIP", "Not", "{", "IN", "in", "OUT", "out", "PARTS:", "Nand", "(", "a", "=",
                "in", "b", "=", "in", "out", "=", "out", ")", "}")
        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun andTest() {
        val input = File("test/hdlsimulator/And.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        val expectedTokens = listOf("CHIP", "And", "{", "IN", "a", "b", "OUT", "out", "PARTS:", "Nand", "(", "a", "=",
                "a", "b", "=", "b", "out", "=", "nandout", ")", "Not", "(", "in", "=", "nandout", "out", "=", "out",
                ")", "}")
        assertEquals(expectedTokens, tokens)
    }
}