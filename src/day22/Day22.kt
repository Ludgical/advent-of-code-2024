package day22

import java.io.File

fun main() {
    val initialNumbers = File("src/day22/input.txt").readLines().map { it.toLong() }.toLongArray()

    println(part1(initialNumbers))
    println(part2(initialNumbers))
}

fun getNext(num: Long): Long {
    var nextNum = num
    nextNum = ((nextNum shl 6) xor nextNum) and 16777215
    nextNum = ((nextNum shr 5) xor nextNum) and 16777215
    nextNum = ((nextNum shl 11) xor nextNum) and 16777215
    return nextNum
}

fun part1(initialNumbers: LongArray): Long {
    var sum = 0L
    for (initialNumber in initialNumbers) {
        var num = initialNumber
        repeat (2000) {
            num = getNext(num)
        }
        sum += num
    }
    return sum
}

typealias Key = Int

fun part2(initialNumbers: LongArray): Int {
    val sequences = Array(initialNumbers.size) { LongArray(2000) }
    for ((index, initialNumber) in initialNumbers.withIndex()) {
        val sequence = sequences[index]
        sequence[0] = initialNumber
        for (i in 1..sequence.lastIndex)
            sequence[i] = getNext(sequence[i - 1])
    }

    val allLastDigits = sequences.map { sequence ->
        sequence.map { it.toInt() % 10 }.toIntArray()
    }.toTypedArray()

    val allDifferences = allLastDigits.map { lastDigits ->
        IntArray(lastDigits.size) { i ->
            if (i == 0) 0
            else lastDigits[i] - lastDigits[i - 1] }
    }.toTypedArray()

    val bananaAmounts = mutableMapOf<Key, Int>()
    val keysThisSequence = mutableSetOf<Key>()

    for (index in sequences.indices) {
        val differences = allDifferences[index]
        val lastDigits = allLastDigits[index]

        for (i in 3 until differences.size) {
            var key = 0
            for (j in -3..0)
                key = (key * 19) + (differences[i + j] + 9)

            if (!keysThisSequence.contains(key)) {
                bananaAmounts[key] = bananaAmounts.getOrDefault(key, 0) + lastDigits[i]
                keysThisSequence.add(key)
            }
        }

        keysThisSequence.clear()
    }

    return bananaAmounts.maxOf { it.value }
}
