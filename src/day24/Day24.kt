package day24

import java.io.File

data class Wire(val name: String, var value: Boolean?, var input: Gate? = null)
data class Gate(val output: Wire, val input1: Wire, val input2: Wire, val type: Char)

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

    val part1 = part1(wires)
    println(part1)
    //The error is where it stops printing ones
    println(binary(part1))
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
    // I printed the gates and found where the pattern broke and got help from the python files

    val outputs = mutableMapOf<Pair<String, String>, MutableSet<String>>()
    for (gate in gates)
        outputs.getOrPut(Pair(gate.input1.name, gate.input2.name)) { mutableSetOf() }.add(gate.output.name)

    val sorted = gates.toList().sortedBy {
        var score = score(it.input1) + score(it.input2).toDouble()
        score += when (it.type) {
            'X' -> 0.0
            'A' -> 0.1
            else -> 0.2
        }
        score
    }.toMutableList()

    val ordered = mutableListOf<Gate>()
    fun addGate(gate: Gate) {
        if (ordered.count { gate.output.name == it.output.name } == 0) {
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
    }

    for (gate in sorted)
        addGate(gate)

    return ordered.joinToString("\n") { "${if (it.input1.name[0] == 'x' && it.type == 'X') '\n' else ""}" +
            "${it.input1.name} ${it.type} ${it.input2.name} -> ${it.output.name}" }
}

fun binary(num: Long): String {
    var highestBit = num.takeHighestOneBit()
    var str = ""
    while (highestBit != 0L) {
        str += if ((num and highestBit) == 0L) '0' else '1'
        highestBit = highestBit shr 1
    }
    return str.ifEmpty { "0" }
}