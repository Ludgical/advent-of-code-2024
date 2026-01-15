package day19

import java.io.File

fun main() {
    val input = File("src/day19/input.txt").readLines()

    val available = input[0].split(", ").toTypedArray()
    val patterns = input.takeLast(input.size - 2).toTypedArray()

    println(part1(available, patterns))
    println(part2(available, patterns))
}

fun part1(available: Array<String>, patterns: Array<String>): Int {
    available.sortBy { it.length }

    fun solvable(pattern: String, startIndex: Int): Boolean {
        if (startIndex == pattern.length)
            return true

        towelLoop@ for (towel in available) {
            for (stripeIndex in towel.indices) {
                if (startIndex + stripeIndex == pattern.length)
                    continue@towelLoop
                if (pattern[startIndex + stripeIndex] != towel[stripeIndex])
                    continue@towelLoop
            }

            if (solvable(pattern, startIndex + towel.length))
                return true
        }

        return false
    }

    var sum = 0
    for (pattern in patterns)
        if (solvable(pattern, 0))
            sum++

    return sum
}

fun part2(available: Array<String>, patterns: Array<String>): Long {
    available.sortBy { it.length }

    val solutionAmounts = mutableMapOf<Pair<String, Int>, Long>()

    fun getSolutionAmount(pattern: String, startIndex: Int): Long {
        if (startIndex == pattern.length)
            return 1

        var solutionAmount = solutionAmounts[Pair(pattern, startIndex)]
        if (solutionAmount != null)
            return solutionAmount

        solutionAmount = 0

        towelLoop@ for (towel in available) {
            for (stripeIndex in towel.indices) {
                if (startIndex + stripeIndex == pattern.length)
                    continue@towelLoop
                if (pattern[startIndex + stripeIndex] != towel[stripeIndex])
                    continue@towelLoop
            }

            solutionAmount += getSolutionAmount(pattern, startIndex + towel.length)
        }

        solutionAmounts[Pair(pattern, startIndex)] = solutionAmount
        return solutionAmount
    }

    var sum = 0L
    for (pattern in patterns)
        sum += getSolutionAmount(pattern, 0)

    return sum
}
