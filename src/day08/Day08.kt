package day08

import java.io.File

fun main() {
    val input = File("src/day08/input.txt").readLines().map { it.toCharArray() }.toTypedArray()
    val grid = Array(input[0].size) { x -> CharArray(input.size) { y -> input[y][x] } }

    val antennaGroupsMap = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

    for (x in grid.indices) {
        for (y in grid[x].indices) {
            val c = grid[x][y]
            if (c != '.') {
                if (!antennaGroupsMap.containsKey(c))
                    antennaGroupsMap[c] = mutableListOf()
                antennaGroupsMap[c]!!.add(Pair(x, y))
            }
        }
    }

    val antennaGroups = antennaGroupsMap.values.map { it.toTypedArray() }.toTypedArray()

    println(part1(grid.map { it.copyOf() }.toTypedArray(), antennaGroups))
    println(part2(grid.map { it.copyOf() }.toTypedArray(), antennaGroups))
}

fun part1(grid: Array<CharArray>, antennaGroups: Array<Array<Pair<Int, Int>>>): Int {
    var sum = 0

    val width = grid[0].size
    val height = grid.size

    for (group in antennaGroups) {
        for (antenna1 in group) {
            for (antenna2 in group) {
                if (antenna1 == antenna2)
                    continue

                val dx = antenna2.first - antenna1.first
                val dy = antenna2.second - antenna1.second
                val newX = antenna2.first + dx
                val newY = antenna2.second + dy

                if (newX < 0 || newY < 0 || newX >= width || newY >= height)
                    continue

                if (grid[newX][newY] != '#') {
                    grid[newX][newY] = '#'
                    sum++
                }
            }
        }
    }

    return sum
}

fun part2(grid: Array<CharArray>, antennaGroups: Array<Array<Pair<Int, Int>>>): Int {
    var sum = 0

    val width = grid[0].size
    val height = grid.size

    for (group in antennaGroups) {
        for (antenna1 in group) {
            for (antenna2 in group) {
                if (antenna1 == antenna2)
                    continue

                val dx = antenna2.first - antenna1.first
                val dy = antenna2.second - antenna1.second
                var newX = antenna2.first
                var newY = antenna2.second

                while (newX >= 0 && newY >= 0 && newX < width && newY < height) {
                    if (grid[newX][newY] != '#') {
                        grid[newX][newY] = '#'
                        sum++
                    }
                    newX += dx
                    newY += dy
                }
            }
        }
    }

    return sum
}