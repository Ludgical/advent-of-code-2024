package day06

import java.io.File

fun main() {
    val input = File("src/day06/input.txt").readText().split("\n").map { it.toCharArray() }

    val map = Array(input[0].size) { x -> BooleanArray(input.size) { y -> input[y][x] == '#' } }

    var guardLocation = Pair(-1, -1)

    for (x in map.indices)
        for (y in map[0].indices)
            if (input[y][x] == '^')
                guardLocation = Pair(x, y)

    println(part1(map, guardLocation))
    println(part2(map, guardLocation))
}

fun part1(map: Array<BooleanArray>, valGuardLocation: Pair<Int, Int>): Int {
    var guardLocation = valGuardLocation
    val visited = mutableSetOf(guardLocation)
    val velocities = arrayOf( Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0) )
    var velocityIndex = 0

    while (true) {
        val nextLocation = Pair(
            guardLocation.first + velocities[velocityIndex].first,
            guardLocation.second + velocities[velocityIndex].second
        )

        println(guardLocation)

        try {
            if (map[nextLocation.first][nextLocation.second]) {
                velocityIndex = (velocityIndex + 1) % 4
            }
            else  {
                guardLocation = Pair(nextLocation.first, nextLocation.second)
                visited.add(guardLocation)
            }
        } catch (_: ArrayIndexOutOfBoundsException) {
            break
        }
    }

    return visited.size
}

fun part2(map: Array<BooleanArray>, valGuardLocation: Pair<Int, Int>): Int {
    var amount = 0

    for (x in map.indices)
        for (y in map[0].indices) {
            if (map[x][y])
                continue

            var guardLocation = valGuardLocation
            val visitedStates = mutableSetOf(Pair(guardLocation, 0))
            val velocities = arrayOf(Pair(0, -1), Pair(1, 0), Pair(0, 1), Pair(-1, 0))
            var velocityIndex = 0
            while (true) {
                val nextLocation = Pair(
                    guardLocation.first + velocities[velocityIndex].first,
                    guardLocation.second + velocities[velocityIndex].second
                )

                try {
                    if (map[nextLocation.first][nextLocation.second] || (nextLocation.first == x && nextLocation.second == y))
                        velocityIndex = (velocityIndex + 1) % 4
                    else {
                        guardLocation = Pair(nextLocation.first, nextLocation.second)
                    }
                } catch (_: ArrayIndexOutOfBoundsException) {
                    break
                }

                val newState = Pair(guardLocation, velocityIndex)
                if (visitedStates.contains(newState)) {
                    amount++
                    break
                }
                else
                    visitedStates.add(newState)
            }
        }

    return amount
}