package hdlsimulator.generator

import hdlsimulator.EXPECTED_NOT_TREE
import org.testng.annotations.Test
import kotlin.test.assertEquals

class GeneratorTests {
    private val generator = Generator()

    @Test
    fun notTest() {
        val notGate = generator.convertNodeToGates(EXPECTED_NOT_TREE)

        val in_ = notGate.ins["in"]!!
        val out = notGate.outs["out"]!!

        in_.value = false
        in_.eval()
        assertEquals(out.value, true)

        in_.value = true
        in_.eval()
        assertEquals(out.value, false)

        in_.value = false
        in_.eval()
        assertEquals(out.value, true)

        in_.value = true
        in_.eval()
        assertEquals(out.value, false)
    }
}