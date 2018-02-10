package hdlsimulator

abstract class Gate {
    var value = false
    val inputs = mutableListOf<Gate>()
    val outputs = mutableListOf<Gate>()
    abstract fun eval()
}

class RegGate : Gate() {
    override fun eval() {
        if (inputs.isNotEmpty()) {
            value = inputs[0].value
        }
        for (node in outputs) {
            node.eval()
        }
    }
}

class NandGate : Gate() {
    override fun eval() {
        value = !(inputs[0].value && inputs[1].value)
        for (node in outputs) {
            node.eval()
        }
    }
}