//package hardwaresimulator.internal.generator
//
//import hardwaresimulator.Chip
//import hardwaresimulator.internal.Gate
//import hardwaresimulator.internal.NandGate
//import hardwaresimulator.internal.PassthroughGate
//import hardwaresimulator.internal.ChipNode
//import hardwaresimulator.internal.PartNode
//
//// TODO: Correct this. Need to use index, and not just name.
//class Generator {
//    fun generateChip(name: String): Chip {
//        val chipGenerator = chipGenerators[name] ?: throw IllegalArgumentException("Unknown chip.")
//        return chipGenerator()
//    }
//
//    fun addChipGenerator(chipNode: ChipNode) {
//        // Stage 1: Create a list of variables used in the chip definition,
//        // including inputs and outputs.
//        val chipVariableNames = getChipNodeVariableNames(chipNode)
//
//        // Step 2: Create a new chip generator.
//        fun chipGenerator(): Chip {
//            // Step 2.1: Map each variable name to a gate.
//            val chipGates = createChipGates(chipVariableNames)
//            // Step 2.2: Create and hook up the chip's parts.
//            generateAndHookUpChipParts(chipNode, chipGates.allGates)
//            // Step 2.3: Create and return chip.
//            return Chip(chipGates.inputGates, chipGates.outputGates)
//        }
//
//        // Step 3: Add the new chip generator to the map of chip generators.
//        chipGenerators.put(chipNode.name, ::chipGenerator)
//    }
//
//    private class ChipVariableNames(
//            val inputNames: List<String>,
//            val outputNames: List<String>,
//            val uniqueInternalVariableNames: List<String>)
//
//    private class ChipGates(
//            val inputGates: Map<String, Gate>,
//            val outputGates: Map<String, Gate>,
//            uniqueInternalVariableGates: Map<String, Gate>) {
//        val allGates = inputGates + outputGates + uniqueInternalVariableGates
//    }
//
//    private fun nandChipGenerator(): Chip {
//        val nandGate = NandGate()
//
//        val in1 = PassthroughGate()
//        nandGate.in1 = in1
//        in1.outputs.add(nandGate)
//
//        val in2 = PassthroughGate()
//        nandGate.in2 = in2
//        in2.outputs.add(nandGate)
//
//        val inputGateMap = mapOf("a" to in1, "b" to in2)
//        val outputGateMap = mapOf("out" to nandGate)
//        return Chip(inputGateMap, outputGateMap)
//    }
//
//    // Known chip generators. Initially, only NAND, the built-in chip, is known.
//    private val chipGenerators = mutableMapOf("Nand" to ::nandChipGenerator)
//
//    private fun getChipNodeVariableNames(chipNode: ChipNode): ChipVariableNames {
//        val inputNames = chipNode.inputs.map { it.name }
//        val outputNames = chipNode.outputs.map { it.name }
//        // All variables used internally by the parts, including the input and
//        // output variables.
//        val internalVariableNames = chipNode.parts.flatMap { part ->
//            part.assignments.map { it.rhs.name }
//        }
//        // All variables used internally by the parts, excluding the input and
//        // output variables.
//        val uniqueInternalVariableNames = internalVariableNames.filter { variableName ->
//            variableName !in inputNames && variableName !in outputNames
//        }
//
//        return ChipVariableNames(inputNames, outputNames, uniqueInternalVariableNames)
//    }
//
//    private fun createChipGates(chipVariableNames: ChipVariableNames): ChipGates {
//        val inputGates = chipVariableNames.inputNames.map { name -> name to PassthroughGate() }.toMap()
//        val outputGates = chipVariableNames.outputNames.map { name -> name to PassthroughGate() }.toMap()
//        val uniqueInternalVariableGates = chipVariableNames.uniqueInternalVariableNames.map { name -> name to PassthroughGate() }.toMap()
//        return ChipGates(inputGates, outputGates, uniqueInternalVariableGates)
//    }
//
//    private fun generateAndHookUpChipParts(chipNode: ChipNode, allGates: Map<String, Gate>) {
//        chipNode.parts.forEach { part ->
//            generateAndHookUpPart(part, allGates)
//        }
//    }
//
//    private fun generateAndHookUpPart(partNode: PartNode, allGates: Map<String, Gate>) {
//        // Step 1.2.1: Create chips for each part.
//        val partGenerator = chipGenerators[partNode.name] ?: throw IllegalArgumentException("Unsupported chip.")
//        val partChip = partGenerator()
//
//        // Step 1.2.2: Hook up all the gates.
//        partNode.assignments.forEach { assignment ->
//            val rhsGate = allGates[assignment.rhs.name] ?: throw IllegalArgumentException("RHS not found in part assignment.")
//
//            when (assignment.lhs.name) {
//                in partChip.inputGateMap -> {
//                    val lhsGate = partChip.inputGateMap[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
//                    lhsGate.in1 = rhsGate
//                    rhsGate.outputs.add(lhsGate)
//                }
//                in partChip.outputGateMap -> {
//                    val lhsGate = partChip.outputGateMap[assignment.lhs.name] ?: throw IllegalArgumentException("LHS not found in part assignment.")
//                    rhsGate.in1 = lhsGate
//                    lhsGate.outputs.add(rhsGate)
//                }
//                else -> throw IllegalArgumentException("LHS not found in chip definition.")
//            }
//        }
//    }
//}