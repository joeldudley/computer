package hdlsimulator.parser

import hdlsimulator.*
import org.junit.Test
import kotlin.test.assertEquals

class ParserTests {
    private val parser = Parser()

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