package day10

import java.io.File
import java.util.ArrayDeque

fun main() {
    val input = File("src/day10/input.txt").readLines().map { it.toCharArray().map { c -> c.digitToInt() } }
    val map = Array(input[0].size) { x -> IntArray(input.size) { y -> input[y][x] } }

    val zeros = mutableListOf<Pair<Int, Int>>()
    for (y in map.indices)
        for (x in map[0].indices)
            if (map[x][y] == 0)
                zeros.add(Pair(x, y))

    println(part1(map, zeros))
    println(part2(map, zeros))
}

fun part1(map: Array<IntArray>, zeros: List<Pair<Int, Int>>): Int {
    var sum = 0

    for (zero in zeros) {
        val nines = mutableSetOf<Pair<Int, Int>>()
        val toExplore = ArrayDeque<Pair<Int, Int>>()
        toExplore.add(zero)

        while (toExplore.isNotEmpty()) {
            val location = toExplore.pop()
            toExplore.remove(location)

            val value = map[location.first][location.second]

            for (offset in arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))) {
                val newX = location.first + offset.first
                val newY = location.second + offset.second

                if (newX !in map[0].indices || newY !in map.indices)
                    continue

                if (map[newX][newY] == value + 1) {
                    val newLocation = Pair(newX, newY)

                    if (value == 8)
                        nines.add(newLocation)
                    else {
                        if (!toExplore.contains(newLocation))
                            toExplore.add(newLocation)
                    }
                }
            }
        }

        sum += nines.size
    }

    return sum
}

fun part2(map: Array<IntArray>, zeros: List<Pair<Int, Int>>): Int {
    //Something similar to part1 also works because the input is so small
    var sum = 0
    val pathsToNineMap = mutableMapOf<Pair<Int, Int>, Int>()
    val offsets = arrayOf(Pair(1, 0), Pair(0, 1), Pair(-1, 0), Pair(0, -1))

    for (zero in zeros)
        sum += pathsToNine(zero, map, pathsToNineMap, offsets)

    return sum
}

fun pathsToNine(location: Pair<Int, Int>, map: Array<IntArray>, pathsToNineMap: MutableMap<Pair<Int, Int>, Int>, offsets: Array<Pair<Int, Int>>): Int {
    val value = map[location.first][location.second]
    if (value == 9)
        return 1

    val amount = pathsToNineMap[location]
    if (amount != null)
        return amount

    var paths = 0
    for (offset in offsets) {
        val newX = location.first + offset.first
        val newY = location.second + offset.second

        if (newX !in map[0].indices || newY !in map.indices)
            continue

        if (map[newX][newY] == value + 1)
            paths += pathsToNine(Pair(newX, newY), map, pathsToNineMap, offsets)
    }
    pathsToNineMap[location] = paths
    return paths
}