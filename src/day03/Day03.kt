package day03

import java.io.File

fun main() {
    val input = File("src/day03/input.txt").readLines()
    val grid = Array(input.size) { i -> input[i].toList().toTypedArray() }
    println(part1(grid))
    println(part2(grid))
}

fun part1(grid: Array<Array<Char>>): Int {
    var sum = 0

    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if (grid[x][y] == 'X') {
                for (dx in -1..1)
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0)
                            continue
                        try {
                            if (grid[x + dx][y + dy] == 'M' && grid[x + 2 * dx][y + 2 * dy] == 'A' && grid[x + 3 * dx][y + 3 * dy] == 'S')
                                sum++
                        } catch (_: ArrayIndexOutOfBoundsException) {}
                    }
            }
        }
    }

    return sum
}

fun part2(grid: Array<Array<Char>>): Int {
    var sum = 0

    for (x in grid.indices) {
        for (y in grid[0].indices) {
            if (grid[x][y] == 'A') {
                try {
                    if (!((grid[x + 1][y + 1] == 'M' || grid[x + 1][y + 1] == 'S') && (grid[x - 1][y - 1] == 'M' || grid[x - 1][y - 1] == 'S')))
                        continue
                    if (grid[x + 1][y + 1] == grid[x - 1][y - 1])
                        continue
                    if ((grid[x + 1][y + 1] == grid[x - 1][y + 1] && grid[x + 1][y - 1] == grid[x - 1][y - 1]) ||
                        (grid[x + 1][y + 1] == grid[x + 1][y - 1] && grid[x - 1][y + 1] == grid[x - 1][y - 1])) {
                        sum++
                    }

                } catch (_: ArrayIndexOutOfBoundsException) {}
            }
        }
    }

    return sum
}