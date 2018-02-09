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

fun NotNode(): Pair<Node, Node> {
    val input = RegNode()
    val output = NandNode()

    input.outputs.add(output)
    output.inputs.add(input)
    output.inputs.add(input)

    return input to output
}

fun AndNode(): Pair<List<Node>, Node> {
    val input1 = RegNode()
    val input2 = RegNode()
    val nand = NandNode()
    val (notIn, notOut) = NotNode()

    input1.outputs.add(nand)
    input2.outputs.add(nand)
    nand.inputs.add(input1)
    nand.inputs.add(input2)

    nand.outputs.add(notIn)
    notIn.inputs.add(nand)

    return listOf(input1, input2) to notOut
}

fun OrNode(): Pair<List<Node>, Node> {
    val input1 = RegNode()
    val input2 = RegNode()
    val (not1In, not1Out) = NotNode()
    val (not2In, not2Out) = NotNode()
    val nand = NandNode()

    input1.outputs.add(not1In)
    not1In.inputs.add(input1)
    input2.outputs.add(not2In)
    not2In.inputs.add(input2)

    not1Out.outputs.add(nand)
    nand.inputs.add(not1Out)
    not2Out.outputs.add(nand)
    nand.inputs.add(not2Out)

    return listOf(input1, input2) to nand
}

fun notTest() {
    val (in_, out) = NotNode()

    in_.value = false
    in_.eval()
    println(out.value)

    in_.value = true
    in_.eval()
    println(out.value)

    in_.value = false
    in_.eval()
    println(out.value)

    in_.value = true
    in_.eval()
    println(out.value)
}

fun andTest() {
    val (ins, out) = AndNode()
    val (in1, in2) = ins[0] to ins[1]

    in1.value = false
    in2.value = false
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = false
    in2.value = true
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = true
    in2.value = false
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = true
    in2.value = true
    in1.eval()
    in2.eval()
    println(out.value)
}

fun orTest() {
    val (ins, out) = OrNode()
    val (in1, in2) = ins[0] to ins[1]

    in1.value = false
    in2.value = false
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = false
    in2.value = true
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = true
    in2.value = false
    in1.eval()
    in2.eval()
    println(out.value)

    in1.value = true
    in2.value = true
    in1.eval()
    in2.eval()
    println(out.value)
}

fun main(args: Array<String>) {
    orTest()
}