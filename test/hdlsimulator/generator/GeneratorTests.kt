package hdlsimulator.generator

import hdlsimulator.EXPECTED_AND_TREE
import hdlsimulator.EXPECTED_NOT_TREE
import org.testng.annotations.Test
import kotlin.test.assertEquals

class GeneratorTests {
    private val generator = Generator()

    @Test
    fun notTest() {
        generator.convertNodeToGates(EXPECTED_NOT_TREE)
        val notGate = generator.knownChips["Not"]!!()

        val in_ = notGate.ins["in"]!!
        val out = notGate.outs["out"]!!

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
        generator.convertNodeToGates(EXPECTED_NOT_TREE)
        generator.convertNodeToGates(EXPECTED_AND_TREE)
        val andGate = generator.knownChips["And"]!!()

        val a = andGate.ins["a"]!!
        val b = andGate.ins["b"]!!
        val out = andGate.outs["out"]!!

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
}