package hardwaresimulator.internal.tokeniser

import hardwaresimulator.internal.AND_HDL_TOKENS
import hardwaresimulator.internal.NOT16_HDL_TOKENS
import hardwaresimulator.internal.NOT_HDL_TOKENS
import hardwaresimulator.internal.OR_HDL_TOKENS
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class TokeniserTests {
    private lateinit var tokenizer: TokenizerImpl

    @Before
    fun before() {
        tokenizer = TokenizerImpl()
    }

    // Test of a small gate (1/3).
    @Test
    fun notTest() {
        val input = File("src/test/resources/Not.hdl").readText()
        val tokens = tokenizer.tokenize(input)
        assertEquals(NOT_HDL_TOKENS, tokens)
    }

    // Test of a small gate (2/3).
    @Test
    fun andTest() {
        val input = File("src/test/resources/And.hdl").readText()
        val tokens = tokenizer.tokenize(input)
        assertEquals(AND_HDL_TOKENS, tokens)
    }

    // Test of a small gate (3/3).
    @Test
    fun orTest() {
        val input = File("src/test/resources/Or.hdl").readText()
        val tokens = tokenizer.tokenize(input)
        assertEquals(OR_HDL_TOKENS, tokens)
    }

    // Test of a multi-input, multi-output gate.
    @Test
    fun not16Test() {
        val input = File("src/test/resources/Not16.hdl").readText()
        val tokens = tokenizer.tokenize(input)
        assertEquals(NOT16_HDL_TOKENS, tokens)
    }

    // TODO: Test of unterminated multi-line comment.
}