package hdlsimulator

import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val simulator = HdlSimulator(listOf("src/test/resources"))

    @Test
    fun notTest() {
        simulator.loadChip("Not")

        simulator.evaluateChip(listOf("in" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("in" to true))
        assertEquals(false, simulator.readValue("out"))

        simulator.evaluateChip(listOf("in" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("in" to true))
        assertEquals(false, simulator.readValue("out"))
    }

    @Test
    fun andTest() {
        simulator.loadChip("And")

        simulator.evaluateChip(listOf("a" to false, "b" to false))
        assertEquals(false, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to false, "b" to true))
        assertEquals(false, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to false))
        assertEquals(false, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to true))
        assertEquals(true, simulator.readValue("out"))
    }

    @Test
    fun orTest() {
        simulator.loadChip("Or")

        simulator.evaluateChip(listOf("a" to false, "b" to false))
        assertEquals(false, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to false, "b" to true))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to true))
        assertEquals(true, simulator.readValue("out"))
    }
}