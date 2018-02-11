package hdlsimulator

abstract class Gate {
    var value = false
    lateinit var input: Gate
    val outputs = mutableListOf<Gate>()
    abstract fun eval()
}

class RegGate : Gate() {
    override fun eval() {
        // TODO: Prevent gates without set inputs triggering exceptions differently.
        try {
            value = input.value
        } catch (e: UninitializedPropertyAccessException) {

        }
        outputs.forEach { it.eval() }
    }
}

class NandGate : Gate() {
    lateinit var auxInput: Gate

    override fun eval() {
        value = !(input.value && auxInput.value)
        outputs.forEach { it.eval() }
    }
}