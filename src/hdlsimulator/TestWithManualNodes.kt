package hdlsimulator

fun NotGate(): Pair<Gate, Gate> {
    val input = RegGate()
    val output = NandGate()

    input.outputs.add(output)
    output.inputs.add(input)
    output.inputs.add(input)

    return input to output
}

fun AndGate(): Pair<List<Gate>, Gate> {
    val input1 = RegGate()
    val input2 = RegGate()
    val nand = NandGate()
    val (notIn, notOut) = NotGate()

    input1.outputs.add(nand)
    input2.outputs.add(nand)
    nand.inputs.add(input1)
    nand.inputs.add(input2)

    nand.outputs.add(notIn)
    notIn.inputs.add(nand)

    return listOf(input1, input2) to notOut
}

fun OrGate(): Pair<List<Gate>, Gate> {
    val input1 = RegGate()
    val input2 = RegGate()
    val (not1In, not1Out) = NotGate()
    val (not2In, not2Out) = NotGate()
    val nand = NandGate()

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
    val (in_, out) = NotGate()

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
    val (ins, out) = AndGate()
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
    val (ins, out) = OrGate()
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