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

    @Test
    fun missingSemiColonAfterInsTest() {
        parser.setInput(MISSING_INS_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after inputs.")
        parser.parse()
    }

    @Test
    fun missingSemiColonAfterOutsTest() {
        parser.setInput(MISSING_OUTS_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after outputs.")
        parser.parse()
    }

    @Test
    fun missingSemiColonAfterComponentTest() {
        parser.setInput(MISSING_COMPONENT_SEMICOLON_TOKENS)

        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Missing semi-colon after component name.")
        parser.parse()
    }

    @Test
    fun notTest() {
        parser.setInput(NOT_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(NOT_CHIP, tree)
    }

    @Test
    fun andTest() {
        parser.setInput(AND_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(AND_CHIP, tree)
    }

    @Test
    fun orTest() {
        parser.setInput(OR_HDL_TOKENS)
        val tree = parser.parse()
        assertEquals(OR_CHIP, tree)
    }
}