package hardwaresimulator.internal.sorter

import hardwaresimulator.internal.AND_HDL_TOKENS
import hardwaresimulator.internal.NOT16_HDL_TOKENS
import hardwaresimulator.internal.NOT_HDL_TOKENS
import hardwaresimulator.internal.OR_HDL_TOKENS
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SorterTests {
    private lateinit var sorter: Sorter

    @Before
    fun before() {
        sorter = Sorter()
    }

    @Test
    fun extractPartNamesTest() {
        val tokensList = listOf(AND_HDL_TOKENS, NOT_HDL_TOKENS, NOT16_HDL_TOKENS, OR_HDL_TOKENS)
        val sortedElements = sorter.orderChipDefinitions(tokensList)

        assertEquals(sortedElements[0].name, "Not")
        assertEquals(sortedElements[1].name, "Or")
        assertEquals(sortedElements[2].name, "Not16")
        assertEquals(sortedElements[3].name, "And")
    }
}