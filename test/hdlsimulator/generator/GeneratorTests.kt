package hdlsimulator.generator

import hdlsimulator.AND_CHIP
import hdlsimulator.NOT_CHIP
import hdlsimulator.OR_CHIP
import org.testng.annotations.Test
import kotlin.test.assertEquals

class GeneratorTests {
    private val generator = Generator()

    @Test
    fun notTest() {
        generator.installChipGenerator(NOT_CHIP)
        val (notInGates, notOutGates) = generator.chipGenerators["Not"]!!()

        val in_ = notInGates["in"]!!
        val out = notOutGates["out"]!!

        in_.value = false
        in_.eval()
        assertEquals(true, out.value)

        in_.value = true
        in_.eval()
        assertEquals(false, out.value)

        in_.value = false
        in_.eval()
        assertEquals(true, out.value)

        in_.value = true
        in_.eval()
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

        a.value = false
        b.value = false
        a.eval()
        b.eval()
        assertEquals(false, out.value)

        a.value = false
        b.value = true
        a.eval()
        b.eval()
        assertEquals(false, out.value)

        a.value = true
        b.value = false
        a.eval()
        b.eval()
        assertEquals(false, out.value)

        a.value = true
        b.value = true
        a.eval()
        b.eval()
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

        a.value = false
        b.value = false
        a.eval()
        b.eval()
        assertEquals(false, out.value)

        a.value = false
        b.value = true
        a.eval()
        b.eval()
        assertEquals(true, out.value)

        a.value = true
        b.value = false
        a.eval()
        b.eval()
        assertEquals(true, out.value)

        a.value = true
        b.value = true
        a.eval()
        b.eval()
        assertEquals(true, out.value)
    }
}