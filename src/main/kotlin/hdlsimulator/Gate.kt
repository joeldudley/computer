package hdlsimulator

open class Gate {
    lateinit var in1: Gate
    val outputs = mutableListOf<Gate>()
    var value = false

    open fun update() {
        value = in1.value
    }
}

class NandGate : Gate() {
    lateinit var in2: Gate

    override fun update() {
        value = !(in1.value && in2.value)
    }
}