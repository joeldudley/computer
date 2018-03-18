package hardwaresimulator.parser

import hardwaresimulator.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.test.assertEquals

class ParserTests {
    private val parser = Parser()

    @get:Rule
    val exception = ExpectedException.none()

    // Test of a small gate (1/3).
    @Test
    fun notTest() {
        parser.setInput(NOT_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(NOT_CHIP, tree)
    }

    // Test of a small gate (2/3).
    @Test
    fun andTest() {
        parser.setInput(AND_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(AND_CHIP, tree)
    }

    // Test of a small gate (3/3).
    @Test
    fun orTest() {
        parser.setInput(OR_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(OR_CHIP, tree)
    }

    // Test of a gate with wide inputs and outputs.
    @Test
    fun not16Test() {
        parser.setInput(NOT16_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(NOT16_CHIP, tree)
    }

    // Test of a missing semi-colon after the inputs.
    @Test
    fun missingSemiColonAfterInsTest() {
        parser.setInput(MISSING_INS_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after inputs.")
        parser.parse()
    }

    // Test of a missing semi-colon after the outputs.
    @Test
    fun missingSemiColonAfterOutsTest() {
        parser.setInput(MISSING_OUTS_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after outputs.")
        parser.parse()
    }

    // Test of a missing semi-colon after a component.
    @Test
    fun missingSemiColonAfterComponentTest() {
        parser.setInput(MISSING_COMPONENT_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after component name.")
        parser.parse()
    }

    // TODO: Test of missing width in brackets in inputs.
    // TODO: Test of missing width in brackets in outputs.
    // TODO: Test of missing width in brackets in parts.
}