package hdlsimulator

// A logic gate.
sealed class Gate {
    lateinit var in1: Gate
    val outputs = mutableListOf<Gate>()
    var value = false

    abstract fun update()
}

// A logic gate with a single input pin and a single output pin.
// value = in1.value
class PassthroughGate: Gate() {
    override fun update() {
        value = in1.value
    }
}

// A logic gate with two input pin and a single output pin.
// value = in1.value nand in2.value
class NandGate : Gate() {
    lateinit var in2: Gate

    override fun update() {
        value = !(in1.value && in2.value)
    }
}