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
        exception.expectMessage("Wrong file extension. Expected .hdl but got .abc.")
        simulator.loadChip("src/test/resources/BadExt.abc")
    }

    @Test
    fun `can specify library chips as files`() {
        simulator.loadLibraryChips("src/test/resources/Not.hdl")
    }

    @Test
    fun `can specify library chips as folders`() {
        simulator.loadLibraryChips("src/test/resources")
    }
}