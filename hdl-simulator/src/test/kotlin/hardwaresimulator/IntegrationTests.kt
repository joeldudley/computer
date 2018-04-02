package hardwaresimulator

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTests {
    private lateinit var simulator: HardwareSimulator

    @Before
    fun before() {
        simulator = HardwareSimulatorImpl()
        simulator.loadChipDefinitions("src/test/resources")
    }

    // Test of a simple non-recursive gate (1/3).
    @Test
    fun notTest() = with(simulator) {
        loadChip("Not")

        setInput("in", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("in", listOf(true))
        assertEquals(listOf(false), getOutput("out"))

        setInput("in", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("in", listOf(true))
        assertEquals(listOf(false), getOutput("out"))
    }

    // Test of a simple non-recursive gate (2/3).
    @Test
    fun andTest() = with(simulator) {
        loadChip("And")

        setInput("a", listOf(false))
        setInput("b", listOf(false))
        assertEquals(listOf(false), getOutput("out"))

        setInput("a", listOf(false))
        setInput("b", listOf(true))
        assertEquals(listOf(false), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(false))
        assertEquals(listOf(false), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(true))
        assertEquals(listOf(true), getOutput("out"))
    }

    // Test of a simple non-recursive gate (3/3).
    @Test
    fun orTest() = with(simulator) {
        loadChip("Or")

        setInput("a", listOf(false))
        setInput("b", listOf(false))
        assertEquals(listOf(false), getOutput("out"))

        setInput("a", listOf(false))
        setInput("b", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(true))
        assertEquals(listOf(true), getOutput("out"))
    }

    // Test of the 3-way nand gate.
    // It's part of the DFF, and no solution is provided as part of Nand2Tetris.
    @Test
    fun nand3WayTest() = with(simulator) {
        loadChip("Nand3Way")

        setInput("a", listOf(false))
        setInput("b", listOf(false))
        setInput("c", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(false))
        setInput("b", listOf(false))
        setInput("c", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(false))
        setInput("b", listOf(true))
        setInput("c", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(false))
        setInput("b", listOf(true))
        setInput("c", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(false))
        setInput("c", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(false))
        setInput("c", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(true))
        setInput("c", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        setInput("a", listOf(true))
        setInput("b", listOf(true))
        setInput("c", listOf(true))
        assertEquals(listOf(false), getOutput("out"))
    }

    // Test of a simple recursive gate.
    @Test
    fun dffTest() = with(simulator) {
        loadChip("DFF")

        // TODO: Annoying set-up code. We need a way to initialise this properly,
        // TODO: or treat the clock input specially.
        setInput("data", listOf(false))
        setInput("clock", listOf(true))

        setInput("data", listOf(false))
        setInput("clock", listOf(false))

        setInput("data", listOf(false))
        setInput("clock", listOf(false))
        assertEquals(listOf(false), getOutput("out"))

        // Output is locked while clock is tocked.
        setInput("data", listOf(true))
        setInput("clock", listOf(false))
        assertEquals(listOf(false), getOutput("out"))

        // Output updates when clock ticks.
        setInput("data", listOf(true))
        setInput("clock", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        // Output is locked while clock is ticked.
        setInput("data", listOf(false))
        setInput("clock", listOf(true))
        assertEquals(listOf(true), getOutput("out"))

        // Output doesn't update when clock tocks.
        setInput("data", listOf(false))
        setInput("clock", listOf(false))
        assertEquals(listOf(true), getOutput("out"))

        // Output updates when clock ticks.
        setInput("data", listOf(false))
        setInput("clock", listOf(true))
        assertEquals(listOf(false), getOutput("out"))
    }

    // Test of a simple gate with wide inputs/outputs.
    @Test
    fun not16Test() = with(simulator) {
        loadChip("Not16")

        setInput("in", (0..15).map { false })
        assertEquals((0..15).map { true }, getOutput("out"))

        setInput("in", (0..15).map { true })
        assertEquals((0..15).map { false }, getOutput("out"))

        setInput("in", (0..7).flatMap { listOf(true, false) })
        assertEquals((0..7).flatMap { listOf(false, true) }, getOutput("out"))
    }

    // Test of a more complex gate with wide inputs/outputs.
    @Test
    fun mux8Way16Test() = with(simulator) {
        loadChip("Mux8Way16")

        // TODO: Set inputs and test conditions.
    }
}