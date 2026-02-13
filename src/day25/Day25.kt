package day25

import java.io.File

fun main() {
    val schematics = File("src/day25/input.txt").readText().split("\n\n")
    val locks = mutableListOf<IntArray>()
    val keys = mutableListOf<IntArray>()

    for (schematic in schematics.map { s -> s.split("\n").map { line -> line.toList() } }) {
        val heights = IntArray(5) { x ->
            var column = 0
            for (y in 1..5)
                if (schematic[y][x] == '#')
                    column++
            column
        }
        if (schematic[0][0] == '#')
            locks.add(heights)
        else
            keys.add(heights)
    }

    println(part1(locks, keys))
}

fun part1(locks: List<IntArray>, keys: List<IntArray>): Int {
    var count = 0
    for (lock in locks) {
        keyLoop@ for (key in keys) {
            for (x in 0 until 5)
                if (lock[x] + key[x] > 5)
                    continue@keyLoop
            count++
        }
    }
    return count
}