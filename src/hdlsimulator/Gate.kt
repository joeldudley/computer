package hdlsimulator

open class Gate {
    var value = false
    lateinit var input: Gate
    val outputs = mutableListOf<Gate>()
    fun eval() {
        outputs.forEach { output ->
            output.update()
            output.eval()
        }
    }

    open fun update() {
        value = input.value
    }
}

class NandGate : Gate() {
    lateinit var auxInput: Gate

    override fun update() {
        value = !(input.value && auxInput.value)
    }
}