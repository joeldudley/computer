package hardwaresimulator

// A logic gate.
sealed class Gate {
    var in1: Gate? = null
    val outputs = mutableListOf<Gate>()
    var value: Boolean = false

    abstract fun calculateNewValue(): Boolean
}

// A logic gate with a single input pin and a single output pin.
// value = in1.value
class PassthroughGate : Gate() {
    override fun calculateNewValue(): Boolean {
        return in1?.value ?: throw IllegalStateException("One of the passthrough gate's inputs is uninitialised.")
    }
}

// A logic gate with two input pin and a single output pin.
// value = in1.value nand in2.value
class NandGate : Gate() {
    var in2: Gate? = null

    override fun calculateNewValue(): Boolean {
        val in1Value = in1?.value ?: throw IllegalStateException("The nand gate's first input is uninitialised.")
        val in2Value = in2?.value ?: throw IllegalStateException("The nand gate's second input is uninitialised.")
        return !(in1Value && in2Value)
    }
}