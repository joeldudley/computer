package hdlsimulator

abstract class Node {
    var value = false
    val inputs = mutableListOf<Node>()
    val outputs = mutableListOf<Node>()
    abstract fun eval()
}

class RegNode : Node() {
    override fun eval() {
        if (inputs.isNotEmpty()) {
            value = inputs[0].value
        }
        for (node in outputs) {
            node.eval()
        }
    }
}

class NandNode : Node() {
    override fun eval() {
        value = !(inputs[0].value && inputs[1].value)
        for (node in outputs) {
            node.eval()
        }
    }
}