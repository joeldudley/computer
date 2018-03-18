package hardwaresimulator.tokeniser

import hardwaresimulator.AND_HDL_TOKENS
import hardwaresimulator.NOT16_HDL_TOKENS
import hardwaresimulator.NOT_HDL_TOKENS
import hardwaresimulator.OR_HDL_TOKENS
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

// TODO: Keep commas in tokens - useful for disambig - and update the tests.

class TokeniserTests {
    private val tokeniser = Tokeniser()

    // Test of a small gate (1/3).
    @Test
    fun notTest() {
        val input = File("src/test/resources/Not.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(NOT_HDL_TOKENS, tokens)
    }

    // Test of a small gate (2/3).
    @Test
    fun andTest() {
        val input = File("src/test/resources/And.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(AND_HDL_TOKENS, tokens)
    }

    // Test of a small gate (3/3).
    @Test
    fun orTest() {
        val input = File("src/test/resources/Or.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(OR_HDL_TOKENS, tokens)
    }

    // Test of a multi-input, multi-output gate.
    @Test
    fun not16Test() {
        val input = File("src/test/resources/Not16.hdl").readText()
        val tokens = tokeniser.tokenize(input)
        assertEquals(NOT16_HDL_TOKENS, tokens)
    }
}