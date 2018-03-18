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

    // Test of the 3-way nand gate.
    // It's a component part of the DFF, and no solution is provided as part of Nand2Tetris.
    @Test
    fun nand3WayTest() {
        simulator.loadChip("Nand3Way")

        simulator.evaluateChip(listOf("a" to false, "b" to false, "c" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to false, "b" to false, "c" to true))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to false, "b" to true, "c" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to false, "b" to true, "c" to true))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to false, "c" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to false, "c" to true))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to true, "c" to false))
        assertEquals(true, simulator.readValue("out"))

        simulator.evaluateChip(listOf("a" to true, "b" to true, "c" to true))
        assertEquals(false, simulator.readValue("out"))
    }

    // Test of a small recursive gate.
    @Test
    fun dffTest() {
        simulator.loadChip("DFF")

        simulator.evaluateChip(listOf("data" to false, "clock" to false))
        assertEquals(false, simulator.readValue("out"))

        // Output is locked while clock is tocked.
        simulator.evaluateChip(listOf("data" to true, "clock" to false))
        assertEquals(false, simulator.readValue("out"))

        // Output updates when clock ticks.
        simulator.evaluateChip(listOf("data" to true, "clock" to true))
        assertEquals(true, simulator.readValue("out"))

        // Output is locked while clock is ticked.
        simulator.evaluateChip(listOf("data" to false, "clock" to true))
        assertEquals(true, simulator.readValue("out"))

        // Output doesn't update when clock tocks.
        simulator.evaluateChip(listOf("data" to false, "clock" to false))
        assertEquals(true, simulator.readValue("out"))

        // Output updates when clock ticks.
        simulator.evaluateChip(listOf("data" to false, "clock" to true))
        assertEquals(false, simulator.readValue("out"))
    }
}