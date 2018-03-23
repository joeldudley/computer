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
        if (in1 == null) {
            throw IllegalStateException("One of the passthrough gate's inputs is uninitialised.")
        }
        return in1!!.value
    }
}

// A logic gate with two input pin and a single output pin.
// value = in1.value nand in2.value
class NandGate : Gate() {
    var in2: Gate? = null

    override fun calculateNewValue(): Boolean {
        if (in1 == null || in2 == null) {
            throw IllegalStateException("One of the nand gate's inputs is uninitialised.")
        }
        return !(in1!!.value && in2!!.value)
    }
}