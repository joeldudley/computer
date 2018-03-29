package hardwaresimulator.internal.generator

import hardwaresimulator.internal.generator.Generator

class GeneratorTests {
    private val generator = Generator()

//    // Test of a small, non-recursive gate (1/3).
//    @Test
//    fun notTest() {
//        simulator.loadChip("Not")
//
//        simulator.setInputs(listOf("in" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("in" to true))
//        assertEquals(false, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("in" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("in" to true))
//        assertEquals(false, simulator.getValue("out"))
//    }
//
//    // Test of a small, non-recursive gate (2/3).
//    @Test
//    fun andTest() {
//        simulator.loadChip("And")
//
//        simulator.setInputs(listOf("a" to false, "b" to false))
//        assertEquals(false, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to false, "b" to true))
//        assertEquals(false, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to false))
//        assertEquals(false, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to true))
//        assertEquals(true, simulator.getValue("out"))
//    }
//
//    // Test of a small, non-recursive gate (3/3).
//    @Test
//    fun orTest() {
//        simulator.loadChip("Or")
//
//        simulator.setInputs(listOf("a" to false, "b" to false))
//        assertEquals(false, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to false, "b" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to true))
//        assertEquals(true, simulator.getValue("out"))
//    }
//
//    // Test of the 3-way nand gate.
//    // It's a part of the DFF, and no solution is provided as part of Nand2Tetris.
//    @Test
//    fun nand3WayTest() {
//        simulator.loadChip("Nand3Way")
//
//        simulator.setInputs(listOf("a" to false, "b" to false, "c" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to false, "b" to false, "c" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to false, "b" to true, "c" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to false, "b" to true, "c" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to false, "c" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to false, "c" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to true, "c" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        simulator.setInputs(listOf("a" to true, "b" to true, "c" to true))
//        assertEquals(false, simulator.getValue("out"))
//    }
//
//    // Test of a small recursive gate.
//    @Test
//    fun dffTest() {
//        simulator.loadChip("DFF")
//
//        simulator.setInputs(listOf("data" to false, "clock" to false))
//        assertEquals(false, simulator.getValue("out"))
//
//        // Output is locked while clock is tocked.
//        simulator.setInputs(listOf("data" to true, "clock" to false))
//        assertEquals(false, simulator.getValue("out"))
//
//        // Output updates when clock ticks.
//        simulator.setInputs(listOf("data" to true, "clock" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        // Output is locked while clock is ticked.
//        simulator.setInputs(listOf("data" to false, "clock" to true))
//        assertEquals(true, simulator.getValue("out"))
//
//        // Output doesn't update when clock tocks.
//        simulator.setInputs(listOf("data" to false, "clock" to false))
//        assertEquals(true, simulator.getValue("out"))
//
//        // Output updates when clock ticks.
//        simulator.setInputs(listOf("data" to false, "clock" to true))
//        assertEquals(false, simulator.getValue("out"))
//    }
//
//    // TODO: Make this pass.
//    // Test of a gate with wide inputs and outputs.
//    @Test
//    fun not16Test() {
//        simulator.loadChip("Not16")
//
//        // TODO: Write test cases. Need to find a way to pass in wide inputs/outputs.
//    }
}