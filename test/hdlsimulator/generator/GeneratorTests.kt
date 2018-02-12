package hdlsimulator.generator

import hdlsimulator.AND_CHIP
import hdlsimulator.NOT_CHIP
import hdlsimulator.OR_CHIP
import hdlsimulator.evaluator.Evaluator
import org.testng.annotations.Test
import kotlin.test.assertEquals

class GeneratorTests {
    private val generator = Generator()
    private val evaluator = Evaluator()

    @Test
    fun notTest() {
        generator.installChipGenerator(NOT_CHIP)
        val (notInGates, notOutGates) = generator.chipGenerators["Not"]!!()

        val in_ = notInGates["in"]!!
        val out = notOutGates["out"]!!

        evaluator.evaluate(listOf(in_ to false))
        assertEquals(true, out.value)

        evaluator.evaluate(listOf(in_ to true))
        assertEquals(false, out.value)

        evaluator.evaluate(listOf(in_ to false))
        assertEquals(true, out.value)

        evaluator.evaluate(listOf(in_ to true))
        assertEquals(false, out.value)
    }

    @Test
    fun andTest() {
        generator.installChipGenerator(NOT_CHIP)
        generator.installChipGenerator(AND_CHIP)
        val (andInGates, andOutGates) = generator.chipGenerators["And"]!!()

        val a = andInGates["a"]!!
        val b = andInGates["b"]!!
        val out = andOutGates["out"]!!

        evaluator.evaluate(listOf(a to false, b to false))
        assertEquals(false, out.value)

        evaluator.evaluate(listOf(a to false, b to true))
        assertEquals(false, out.value)

        evaluator.evaluate(listOf(a to true, b to false))
        assertEquals(false, out.value)

        evaluator.evaluate(listOf(a to true, b to true))
        assertEquals(true, out.value)
    }

    @Test
    fun orTest() {
        generator.installChipGenerator(NOT_CHIP)
        generator.installChipGenerator(AND_CHIP)
        generator.installChipGenerator(OR_CHIP)
        val (orInGates, orOutGates) = generator.chipGenerators["Or"]!!()

        val a = orInGates["a"]!!
        val b = orInGates["b"]!!
        val out = orOutGates["out"]!!

        evaluator.evaluate(listOf(a to false, b to false))
        assertEquals(false, out.value)

        evaluator.evaluate(listOf(a to false, b to true))
        assertEquals(true, out.value)

        evaluator.evaluate(listOf(a to true, b to false))
        assertEquals(true, out.value)

        evaluator.evaluate(listOf(a to true, b to true))
        assertEquals(true, out.value)
    }
}