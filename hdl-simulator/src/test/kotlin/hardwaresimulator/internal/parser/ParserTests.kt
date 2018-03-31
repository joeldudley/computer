package hardwaresimulator.internal.parser

import hardwaresimulator.internal.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertEquals

class ParserTests {
    private lateinit var parser: Parser

    @Before
    fun before() {
        parser = ParserImpl()
        listOf(NOT_HDL_TOKENS, AND_HDL_TOKENS, OR_HDL_TOKENS, NOT16_HDL_TOKENS).forEach {
            tokens -> parser.parseAndCache(tokens)
        }
    }

    @get:Rule
    val exception = ExpectedException.none()

    // Test of a small gate (1/3).
    @Test
    fun notTest() {
        val tree = parser.retrieve("Not")
        assertEquals(NOT_CHIP, tree)
    }

    // Test of a small gate (2/3).
    @Test
    fun andTest() {
        val tree = parser.retrieve("And")
        assertEquals(AND_CHIP, tree)
    }

    // Test of a small gate (3/3).
    @Test
    fun orTest() {
        val tree = parser.retrieve("Or")
        assertEquals(OR_CHIP, tree)
    }

    // Test of a gate with wide inputs and outputs, and wide RHSs in the
    // parts.
    @Test
    fun not16Test() {
        val tree = parser.retrieve("Not16")
        assertEquals(NOT16_CHIP, tree)
    }

    // Test of a missing semi-colon after the inputs.
    @Test
    fun missingSemiColonAfterInsTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after pins.")
        parser.parseAndCache(MISSING_INS_SEMICOLON_TOKENS)
    }

    // Test of a missing semi-colon after the outputs.
    @Test
    fun missingSemiColonAfterOutsTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after pins.")
        parser.parseAndCache(MISSING_OUTS_SEMICOLON_TOKENS)
    }

    // Test of a missing semi-colon after a part.
    @Test
    fun missingSemiColonAfterPartTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after part name.")
        parser.parseAndCache(MISSING_PART_SEMICOLON_TOKENS)
    }

    // Test of a missing 'CHIP' token.
    @Test
    fun missingChipTokenTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token CHIP, got token NA.")
        parser.parseAndCache(MISSING_CHIP_TOKEN_TOKENS)
    }

    // Test of a missing 'IN' token.
    @Test
    fun missingInTokenTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token IN, got token in.")
        parser.parseAndCache(MISSING_IN_TOKEN_TOKENS)
    }

    // Test of a missing 'OUT' token.
    @Test
    fun missingOutTokenTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token OUT, got token out.")
        parser.parseAndCache(MISSING_OUT_TOKEN_TOKENS)
    }

    // Test of a missing 'PARTS' token.
    @Test
    fun missingPartsTokenTest() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token PARTS:, got token Nand.")
        parser.parseAndCache(MISSING_PARTS_TOKEN_TOKENS)
    }

    // Tests that the parser doesn't throw for an empty inputs list.
    @Test
    fun noInputTokensTest() {
        parser.parseAndCache(NO_INPUTS_TOKENS)
    }

    // Tests that the parser doesn't throw for an empty outputs list.
    @Test
    fun noOutputTokensTest() {
        parser.parseAndCache(NO_OUTPUTS_TOKENS)
    }

    // Tests that the parser doesn't throw for an empty parts list.
    @Test
    fun noPartTokensTest() {
        parser.parseAndCache(NO_PARTS_TOKENS)
    }
}