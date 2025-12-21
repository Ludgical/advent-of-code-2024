package day07

import java.io.File

fun main() {
    val equations = File("src/day07/input.txt").readLines().map { line ->
        val parts = line.split(": ")
        val result = parts[0].toLong()
        val numbers = parts[1].split(" ").map { it.toInt() }.toIntArray()
        Pair(result, numbers)
    }.toTypedArray()

    println(part1(equations))
    println(part2(equations))
}

fun part1(equations: Array<Pair<Long, IntArray>>): Long {
    var sum = 0L

    for (equation in equations) {
        if (canBeTrue(equation, 0, 0))
            sum += equation.first
    }

    return sum
}

fun canBeTrue(equation: Pair<Long, IntArray>, nextNumIndex: Int, calculated: Long): Boolean {
    if (nextNumIndex == equation.second.size)
        return equation.first == calculated

    if (calculated > equation.first)
        return false

    return canBeTrue(equation, nextNumIndex + 1, calculated * equation.second[nextNumIndex]) ||
           canBeTrue(equation, nextNumIndex + 1, calculated + equation.second[nextNumIndex])
}

fun part2(equations: Array<Pair<Long, IntArray>>): Long {
    var sum = 0L

    for (equation in equations) {
        if (canBeTrueConcatenation(equation, 1, equation.second[0].toLong()))
            sum += equation.first
    }

    return sum
}

fun canBeTrueConcatenation(equation: Pair<Long, IntArray>, nextNumIndex: Int, calculated: Long): Boolean {
    if (nextNumIndex == equation.second.size)
        return equation.first == calculated

    if (calculated > equation.first)
        return false

    return canBeTrueConcatenation(equation, nextNumIndex + 1, (calculated.toString() + equation.second[nextNumIndex].toString()).toLong()) ||
           canBeTrueConcatenation(equation, nextNumIndex + 1, calculated * equation.second[nextNumIndex]) ||
           canBeTrueConcatenation(equation, nextNumIndex + 1, calculated + equation.second[nextNumIndex])
}