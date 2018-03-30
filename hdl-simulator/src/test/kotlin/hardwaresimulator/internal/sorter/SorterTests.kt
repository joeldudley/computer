package hardwaresimulator.internal.sorter

import hardwaresimulator.internal.tokeniser.Tokenizer
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class SorterTests {
    private lateinit var sorter: Sorter

    @Before
    fun before() {
        sorter = Sorter()
    }

    // TODO: Add more HDL definitions and add a more granular test.

    @Test
    fun extractPartNamesTest() {
        val tokeniser = Tokenizer()

        val files = File("src/test/resources").listFiles()
        val hdlFiles = files.filter { file -> file.extension == "hdl" }
        val hdlFilesText = hdlFiles.map { it.readText() }
        val tokensList = hdlFilesText.map { tokeniser.tokenize(it) }

        val sortedElements = sorter.orderChipDefinitions(tokensList)
        assertEquals(sortedElements[0].name, "Not")
        assertEquals(sortedElements[1].name, "Or")
        assertEquals(sortedElements[2].name, "Not16")
        assertEquals(sortedElements[3].name, "And")
        assertEquals(sortedElements[4].name, "Nand3Way")
        assertEquals(sortedElements[5].name, "DFF")
    }
}