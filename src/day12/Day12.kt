package day12

import java.io.File

fun main() {
    val input = File("src/day12/input.txt").readLines()
    val map = Array(input[0].length) { x -> CharArray(input.size) { y -> input[y][x] } }

    println(part1(map))
    println(part2(map))
}

fun part1(map: Array<CharArray>): Int {
    val explored = Array(map.size) { BooleanArray(map[0].size) { false } }

    var sum = 0

    for (y in map[0].indices) {
        for (x in map.indices) {
            if (explored[x][y])
                continue

            explored[x][y] = true

            val (area, perimeter) = getAreaAndPerimeter(x, y, map, explored)
            sum += area * perimeter
        }
    }

    return sum
}

fun getAreaAndPerimeter(x: Int, y: Int, map: Array<CharArray>, explored: Array<BooleanArray>): Pair<Int, Int> {
    val type = map[x][y]
    val width = map.size
    val height = map[0].size
    var area = 0
    var perimeter = 0

    val start = Pair(x, y)
    val toExplore = ArrayDeque<Pair<Int, Int>>()
    toExplore.add(start)

    val offsets = arrayOf(Pair(1,0), Pair(0,1), Pair(-1,0), Pair(0,-1))

    while (toExplore.isNotEmpty()) {
        val location = toExplore.removeFirst()
        area++
        for (offset in offsets) {
            val newLocation = Pair(location.first + offset.first, location.second + offset.second)
            val newX = newLocation.first
            val newY = newLocation.second

            if (newX !in 0..<width || newY !in 0..<height) {
                perimeter++
                continue
            }

            if (map[newX][newY] != type) {
                perimeter++
                continue
            }

            if (explored[newX][newY])
                continue

            if (toExplore.contains(newLocation))
                continue

            toExplore.add(newLocation)
            explored[newX][newY] = true
        }
    }
    return Pair(area, perimeter)
}

fun part2(map: Array<CharArray>): Int {
    val explored = Array(map.size) { BooleanArray(map[0].size) { false } }

    var sum = 0

    for (y in map[0].indices) {
        for (x in map.indices) {
            if (explored[x][y])
                continue

            explored[x][y] = true

            val (area, sides) = getAreaAndSides(x, y, map, explored)
            sum += area * sides
        }
    }

    return sum
}

fun getAreaAndSides(x: Int, y: Int, map: Array<CharArray>, explored: Array<BooleanArray>): Pair<Int, Int> {
    val type = map[x][y]
    val width = map.size
    val height = map[0].size
    var area = 0

    val horizontalFences = mutableListOf<Pair<Pair<Int, Int>, Boolean>>()
    val verticalFences = mutableListOf<Pair<Pair<Int, Int>, Boolean>>()

    val start = Pair(x, y)
    val toExplore = ArrayDeque<Pair<Int, Int>>()
    toExplore.add(start)

    val offsets = arrayOf(Pair(1,0), Pair(0,1), Pair(-1,0), Pair(0,-1))

    while (toExplore.isNotEmpty()) {
        val location = toExplore.removeFirst()
        area++
        for (offset in offsets) {
            val newLocation = Pair(location.first + offset.first, location.second + offset.second)
            val newX = newLocation.first
            val newY = newLocation.second

            if (newX !in 0..<width || newY !in 0..<height || map[newX][newY] != type) {
                //The boolean shows if the fence comes from a square on the right or left / top or bottom of it
                if (offset.first == 1)
                    verticalFences.add(Pair(Pair(newX, newY), true))
                else if (offset.first == -1)
                    verticalFences.add(Pair(Pair(newX + 1, newY), false))
                else if (offset.second == 1)
                    horizontalFences.add(Pair(Pair(newX, newY), true))
                else if (offset.second == -1)
                    horizontalFences.add(Pair(Pair(newX, newY + 1), false))

                continue
            }

            if (explored[newX][newY] || toExplore.contains(newLocation))
                continue

            toExplore.add(newLocation)
            explored[newX][newY] = true
        }
    }

    fun removeLeftFence(fence: Pair<Pair<Int, Int>, Boolean>) {
        val leftFence = Pair(Pair(fence.first.first - 1, fence.first.second), fence.second)
        if (horizontalFences.remove(leftFence))
            removeLeftFence(leftFence)
    }
    fun removeRightFence(fence: Pair<Pair<Int, Int>, Boolean>) {
        val rightFence = Pair(Pair(fence.first.first + 1, fence.first.second), fence.second)
        if (horizontalFences.remove(rightFence))
            removeRightFence(rightFence)
    }
    fun removeUpperFence(fence: Pair<Pair<Int, Int>, Boolean>) {
        val upperFence = Pair(Pair(fence.first.first, fence.first.second - 1), fence.second)
        if (verticalFences.remove(upperFence))
            removeUpperFence(upperFence)
    }
    fun removeLowerFence(fence: Pair<Pair<Int, Int>, Boolean>) {
        val lowerFence = Pair(Pair(fence.first.first, fence.first.second + 1), fence.second)
        if (verticalFences.remove(lowerFence))
            removeLowerFence(lowerFence)
    }

    var sideAmount = 0

    while (horizontalFences.isNotEmpty()) {
        val fence = horizontalFences.removeFirst()
        removeLeftFence(fence)
        removeRightFence(fence)
        sideAmount++
    }
    while (verticalFences.isNotEmpty()) {
        val fence = verticalFences.removeFirst()
        removeUpperFence(fence)
        removeLowerFence(fence)
        sideAmount++
    }

    return Pair(area, sideAmount)
}