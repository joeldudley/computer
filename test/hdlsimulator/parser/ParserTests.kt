package hdlsimulator.parser

import hdlsimulator.*
import org.testng.annotations.Test
import kotlin.test.assertEquals

class ParserTests {
    private val parser = Parser()

    @Test
    fun notTest() {
        parser.setInput(EXPECTED_NOT_TOKENS)
        val tree = parser.parse()
        assertEquals(EXPECTED_NOT_TREE, tree)
    }

    @Test
    fun andTest() {
        parser.setInput(EXPECTED_AND_TOKENS)
        val tree = parser.parse()
        assertEquals(EXPECTED_AND_TREE, tree)
    }

    @Test
    fun orTest() {
        parser.setInput(EXPECTED_OR_TOKENS)
        val tree = parser.parse()
        assertEquals(EXPECTED_OR_TREE, tree)
    }
}