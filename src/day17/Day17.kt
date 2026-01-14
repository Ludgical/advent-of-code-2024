package day17

import java.io.File

fun main() {
    val input = File("src/day17/input.txt").readLines()

    val registers = LongArray(3) { i ->
        input[i].split(":")[1].trim().toLong()
    }
    val rawProgram = input[4].split(":")[1].trim().split(",").map { it.toInt() }.toIntArray()
    val program = Array(rawProgram.size / 2) { i -> Pair(rawProgram[i * 2], rawProgram[i * 2 + 1]) }

    println(part1(registers, program).joinToString(","))
    val part2result = part2(program, rawProgram)
    println(part2result)
    println(rawProgram.joinToString(","))
    println(part1(longArrayOf(part2result, 0, 0), program).joinToString(","))
}

fun part1(registers: LongArray, program: Array<Pair<Int, Int>>): List<Int> {
    var (regA, regB, regC) = registers

    fun comboOp(op: Int): Long = when (op) {
        4 -> regA
        5 -> regB
        6 -> regC
        else -> op.toLong()
    }

    val output = mutableListOf<Int>()
    var location = 0
    var nextLocation: Int
    while (location < program.size) {
        val instruction = program[location]
        val opcode = instruction.first
        val operand = instruction.second
        nextLocation = location + 1

        when (opcode) {
            0 -> regA /= (1L shl comboOp(operand).toInt())
            1 -> regB = regB.xor(operand.toLong())
            2 -> regB = comboOp(operand) % 8
            3 -> if (regA != 0L) nextLocation = operand
            4 -> regB = regB.xor(regC)
            5 -> output.add(comboOp(operand).toInt() % 8)
            6 -> regB = regA / (1L shl comboOp(operand).toInt())
            7 -> regC = regA / (1L shl comboOp(operand).toInt())
        }

        location = nextLocation
    }

    return output
}

fun part2(program: Array<Pair<Int, Int>>, rawProgram: IntArray): Long {
    return solveNumber(0, 0L, program, rawProgram)
}

fun solveNumber(n: Int, aValue: Long, program: Array<Pair<Int, Int>>, expected: IntArray): Long {
    if (n == expected.size)
        return aValue

    for (v in 0b000..0b111) {
        val newAValue = (aValue shl 3) + v
        val output = part1(longArrayOf(newAValue, 0, 0), program)
        if (output[0] != expected[expected.lastIndex - n])
            continue

        val solution = solveNumber(n + 1, newAValue, program, expected)
        if (solution != -1L)
            return solution
    }
    return -1
}