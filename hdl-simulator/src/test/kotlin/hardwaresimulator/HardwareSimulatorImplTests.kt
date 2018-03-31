package hardwaresimulator

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class HardwareSimulatorImplTests {
    private val simulator = HardwareSimulatorImpl()

    @get:Rule
    val exception = ExpectedException.none()

    @Test
    fun `loading non-hdl file as chip throws error`() {
        exception.expect(IllegalArgumentException::class.java)
        exception.expectMessage("Chip BadExt is not cached.")
        simulator.loadChip("BadExt")
    }

    @Test
    fun `can specify chip definitions as files`() {
        simulator.loadChipDefinitions("src/test/resources/Not.hdl")
    }

    @Test
    fun `can specify chip definitions as folders`() {
        simulator.loadChipDefinitions("src/test/resources")
    }
}