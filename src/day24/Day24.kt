package day24

import java.io.File

data class Wire(var name: String, var value: Boolean?, var input: Gate? = null)
data class Gate(val output: Wire, val input1: Wire, val input2: Wire, val type: Char) {
    override fun toString() = "${input1.name} $type ${input2.name} -> ${output.name}"
}

fun score(wire: Wire): Int {
    val score = when (wire.name[0]) {
        'x' -> 100
        'y' -> 200
        'z' -> 300
        else -> return wire.name.hashCode()
    }
    return score + wire.name.takeLast(2).toInt()
}

fun main() {
    val (initialValuesStr, gatesStr) = File("src/day24/input.txt").readText().replace("\r", "").split("\n\n").map { it.split("\n") }

    val wires = initialValuesStr.associate {
        val (name, value) = it.split(": ")
        name to Wire(name, value == "1")
    }.toMutableMap()

    val gates = gatesStr.map {
        val (input1, type, input2, _, output) = it.split(" ")
        val outputWire = wires.getOrPut(output) { Wire(output, null) }
        val inputWire1 = wires.getOrPut(input1) { Wire(input1, null) }
        val inputWire2 = wires.getOrPut(input2) { Wire(input2, null) }

        val (input1Wire, input2Wire) = if (score(inputWire1) < score(inputWire2)) Pair(inputWire1, inputWire2) else Pair(inputWire2, inputWire1)

        val gate = Gate(outputWire, input1Wire, input2Wire, type[0])
        outputWire.input = gate
        wires[output] = outputWire
        gate
    }

    println(part1(wires))
    println(part2(gates))
}

fun calc(gate: Gate): Boolean {
    val input1 = calc(gate.input1)
    val input2 = calc(gate.input2)

    return when (gate.type) {
        'A' -> input1 && input2
        'O' -> input1 || input2
        'X' -> input1 != input2
        else -> false
    }
}
fun calc(wire: Wire): Boolean {
    if (wire.value != null)
        return wire.value == true

    return calc(wire.input!!)
}

fun part1(wires: Map<String, Wire>): Long {
    var sum = 0L
    var zIndex = wires.maxOf { if (it.key[0] == 'z') it.key.takeLast(2).toInt() else 0 }
    while (true) {
        val zWireStr = if (zIndex >= 10) "z$zIndex" else "z0$zIndex"
        val zWire = wires[zWireStr] ?: return sum
        val zWireResult = calc(zWire)

        sum = sum shl 1
        sum += if (zWireResult) 1 else 0

        wires[zWireStr]!!.value = zWireResult

        zIndex--
    }
}

fun part2(gates: List<Gate>): String {
    // Sort gates so that it goes x-bits, y-bits, z-bits, rest sorted alphabetically
    val sorted = gates.toList().sortedBy { score(it.input1) + score(it.input2).toDouble() }.toMutableList()

    // Put the gates in the order they would execute (naturally puts the gates in groups)
    val ordered = mutableListOf<Gate>()
    fun addGate(gate: Gate) {
        if (ordered.count { gate.output.name == it.output.name } != 0)
            return

        ordered.add(gate)
        for (nextGate in sorted) {
            if (nextGate.input1.name == gate.output.name) {
                if (ordered.count { nextGate.input2.name == it.output.name } != 0)
                    addGate(nextGate)
            }
            else if (nextGate.input2.name == gate.output.name) {
                if (ordered.count { nextGate.input1.name == it.output.name } != 0)
                    addGate(nextGate)
            }
        }

    }

    for (gate in sorted)
        addGate(gate)

    val swapped = mutableSetOf<String>()

    // I assume the first 2 lines are always correct and that the swaps only happen within a single group or in the carry
    // Should be:
    // x00 X y00 -> z00
    // x00 A y00 -> carry
    // Repeat:
    // xn X yn -> a
    // a X carry -> zn
    // a A carry -> b
    // xn A yn -> c
    // b O c -> carry

    var carry = ordered[1].output.name

    for (n in 1 until ordered.maxOf { it.output.name.takeLast(2).toIntOrNull()?: -1 }) {
        // Sort gates withing the group (all the groups should have the same structure)
        val group = Array(5) { i ->
            ordered[(5 * n) - 3 + i] }.sortedBy {
                when (it.type) {
                    'X' -> 0
                    'A' -> 1 shl 24
                    else -> 2 shl 24
                } + score(it.input1) + score(it.input2)
            }

        // Check if every output goes to the inputs it should go to, if it does not, add the output the swapped

        var gate = group[0]
        val a = gate.output.name

        gate = group[1]
        if (gate.input1.name != a && gate.input2.name != a)
            swapped.add(a)
        if (gate.input1.name != carry && gate.input2.name != carry)
            swapped.add(carry)
        if (gate.output.name[0] != 'z')
            swapped.add(gate.output.name)

        gate = group[2]
        // a and carry would've gotten caught in the previous gate
        val b = gate.output.name

        gate = group[3]
        val c = gate.output.name

        gate = group[4]
        if (gate.input1.name != b && gate.input2.name != b)
            swapped.add(b)
        if (gate.input1.name != c && gate.input2.name != c)
            swapped.add(c)
        carry = gate.output.name
    }

    return swapped.sortedBy { it.hashCode() }.joinToString(",")
}