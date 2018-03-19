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

    // TODO: Split this into more targeted tests by using dummy chips (correct width gathered in each part, input only has width, output only has width, lhs width, rhs width)
    // Test of a gate with wide inputs and outputs, and wide RHSs in the
    // components.
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
        exception.expectMessage("Missing semi-colon after pins.")
        parser.parse()
    }

    // Test of a missing semi-colon after the outputs.
    @Test
    fun missingSemiColonAfterOutsTest() {
        parser.setInput(MISSING_OUTS_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after pins.")
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

    // Test of a missing 'CHIP' token.
    @Test
    fun missingChipTokenTest() {
        parser.setInput(MISSING_CHIP_TOKEN_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token CHIP, got token NA.")
        parser.parse()
    }

    // Test of a missing 'IN' token.
    @Test
    fun missingInTokenTest() {
        parser.setInput(MISSING_IN_TOKEN_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token IN, got token in.")
        parser.parse()
    }

    // Test of a missing 'OUT' token.
    @Test
    fun missingOutTokenTest() {
        parser.setInput(MISSING_OUT_TOKEN_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token OUT, got token out.")
        parser.parse()
    }

    // Test of a missing 'PARTS' token.
    @Test
    fun missingPartsTokenTest() {
        parser.setInput(MISSING_PARTS_TOKEN_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Expected token PARTS:, got token Nand.")
        parser.parse()
    }

    // Tests that the parser doesn't throw for an empty inputs list.
    @Test
    fun noInputTokensTest() {
        parser.setInput(NO_INPUTS_TOKENS)
        parser.parse()
    }

    // Tests that the parser doesn't throw for an empty outputs list.
    @Test
    fun noOutputTokensTest() {
        parser.setInput(NO_OUTPUTS_TOKENS)
        parser.parse()
    }

    // Tests that the parser doesn't throw for an empty components list.
    @Test
    fun noComponentTokensTest() {
        parser.setInput(NO_COMPONENTS_TOKENS)
        parser.parse()
    }

    // TODO: Test of missing width in brackets in inputs.
    // TODO: Test of missing width in brackets in outputs.
    // TODO: Test of missing width in brackets in parts.
}