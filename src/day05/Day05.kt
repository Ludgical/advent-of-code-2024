package day05

import java.io.File

fun main() {
    val input = File("src/day05/input.txt").readText()
    val (rulesStr, updatesStr) = input.split("\n\n").map { it.split("\n") }
    val rules = Array(rulesStr.size ) { i ->
        val nums = rulesStr[i].split('|')
        Pair(nums[0].toInt(), nums[1].toInt())
    }
    val updates = Array(updatesStr.size ) { i ->
        updatesStr[i].split(',').map{ it.toInt() }.toTypedArray()
    }
    println(part1(rules, updates))
    println(part2(rules, updates))
}

fun part1(rules: Array<Pair<Int, Int>>, updates: Array<Array<Int>>): Int {
    var sum = 0

    for (update in updates) {
        var allowed = true

        for (rule in rules) {
            val firstNumIndex = update.indexOf(rule.first)
            if (firstNumIndex != -1) {
                val secondNumIndex = update.indexOf(rule.second)
                if (secondNumIndex != -1) {
                    if (firstNumIndex > secondNumIndex) {
                        allowed = false
                        break
                    }
                }

            }
        }

        if (allowed)
            sum += update[update.size shr 1]
    }

    return sum
}

fun part2(rules: Array<Pair<Int, Int>>, updates: Array<Array<Int>>): Int {
    var sum = 0

    for (update in updates) {
        var allowed = true

        do {
            var somethingSwapped = false
            for (rule in rules) {
                val firstNumIndex = update.indexOf(rule.first)
                if (firstNumIndex != -1) {
                    val secondNumIndex = update.indexOf(rule.second)
                    if (secondNumIndex != -1) {
                        if (firstNumIndex > secondNumIndex) {
                            allowed = false
                            somethingSwapped = true
                            val temp = update[firstNumIndex]
                            update[firstNumIndex] = update[secondNumIndex]
                            update[secondNumIndex] = temp
                        }
                    }

                }
            }
        } while (somethingSwapped)

        if (!allowed)
            sum += update[update.size shr 1]
    }

    return sum
}