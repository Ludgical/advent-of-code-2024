package day11

import java.io.File

fun main() {
    val stones = File("src/day11/input.txt").readText().split(' ').map { it.toLong() }

    println(part1(stones))
    println(part2(stones))
}

fun part1(firstStones: List<Long>): Int {
    var stones = firstStones.toList()
    repeat(25) {
        val newStones = mutableListOf<Long>()

        for (stone in stones) {
            if (stone == 0L) {
                newStones.add(1)
                continue
            }

            val strStone = stone.toString()
            if (strStone.length % 2 == 0) {
                val half = strStone.length / 2
                newStones.add(strStone.take(half).toLong())
                newStones.add(strStone.takeLast(half).toLong())
                continue
            }

            newStones.add(stone * 2024)
        }

        stones = newStones
    }
    return stones.size
}

fun part2(stones: List<Long>): Long {
    var sum = 0L
    val map = mutableMapOf<Pair<Long, Int>, Long>()
    for (stone in stones) {
        sum += getNewStoneAmount(stone, 75, map) + 1
    }
    return sum
}

fun getNewStoneAmount(stone: Long, afterBlinks: Int, map: MutableMap<Pair<Long, Int>, Long>): Long {
    if (afterBlinks == 0)
        return 0

    var newStoneAmount = map[Pair(stone, afterBlinks)]
    if (newStoneAmount != null)
        return newStoneAmount

    //Calculate new stone amount recursively
    if (stone == 0L) {
        newStoneAmount = getNewStoneAmount(1, afterBlinks - 1, map)
        map[Pair(stone, afterBlinks)] = newStoneAmount
        return newStoneAmount
    }

    val strStone = stone.toString()
    if (strStone.length.and(1) == 0) {
        val half = strStone.length shr 1
        newStoneAmount = (
                getNewStoneAmount(strStone.take(half).toLong(), afterBlinks - 1, map) +
                getNewStoneAmount(strStone.takeLast(half).toLong(), afterBlinks - 1, map) +
                1
        )
        return newStoneAmount
    }

    newStoneAmount = getNewStoneAmount(stone * 2024, afterBlinks - 1, map)
    map[Pair(stone, afterBlinks)] = newStoneAmount
    return newStoneAmount
}