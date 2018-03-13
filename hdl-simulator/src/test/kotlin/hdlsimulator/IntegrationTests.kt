package hdlsimulator

import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private val simulator = HdlSimulator(listOf("src/test/resources"))

    // Test of a small, non-recursive gate (1/3).
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

    // Test of a small, non-recursive gate (2/3).
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

    // Test of a small, non-recursive gate (3/3).
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

    // Test of a small recursive gate.
    @Test
    fun dffTest() {
        TODO("Write flip-flop test.")
    }
}