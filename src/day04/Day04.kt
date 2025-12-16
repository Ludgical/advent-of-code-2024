package day04

import java.io.File

fun main() {
    val memory = File("src/day04/input.txt").readText()
    println(part1(memory))
    println(part2(memory))
}

fun part1(memory: String): Int {
    var sum = 0

    val opens = mutableListOf<Int>()
    for ((i, c) in memory.withIndex()) {
        if (c == '(')
            opens.add(i)
    }

    for (open in opens) {
        if (memory.substring(open - 3, open) != "mul")
            continue
        val nextClose = memory.indexOf(')', open)
        try {
            val str = memory.substring(open + 1, nextClose)
            val strNums = str.split(",")
            val nums = mutableListOf<Int>()
            for (num in strNums)
                nums.add(num.toInt())
            sum += nums[0] * nums[1]
        } catch (_: Exception) {}
    }

    return sum
}

fun part2(memory: String): Int {
    var sum = 0

    val opens = mutableListOf<Int>()

    for ((i, c) in memory.withIndex()) {
        if (c == '(')
            opens.add(i)
    }

    val dos = mutableListOf<Int>()
    try {
        for (i in memory.indices) {
            if (memory.substring(i, i + 4) == "do()")
                dos.add(i)
        }
    } catch (_: StringIndexOutOfBoundsException) {}

    val donts = mutableListOf<Int>()
    try {
        for (i in memory.indices) {
            if (memory.substring(i, i + 7) == "don't()")
                donts.add(i)
        }
    } catch (_: StringIndexOutOfBoundsException) {}

    println(dos)
    println(donts)

    var lastDo = true

    for (i in memory.indices) {
        if (dos.contains(i))
            lastDo = true
        if (donts.contains(i))
            lastDo = false
        if (!lastDo)
            continue
        if (!opens.contains(i))
            continue
        if (memory.substring(i - 3, i) != "mul")
            continue
        val nextClose = memory.indexOf(')', i)
        try {
            val str = memory.substring(i + 1, nextClose)
            val strNums = str.split(",")
            val nums = mutableListOf<Int>()
            for (num in strNums)
                nums.add(num.toInt())
            sum += nums[0] * nums[1]
        } catch (_: Exception) {}
    }

    return sum
}