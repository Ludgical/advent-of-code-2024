package day20

import java.io.File
import kotlin.math.abs

typealias Location = Pair<Int, Int>

fun main() {
    val input = File("src/day20/input.txt").readLines()

    val maze = LongArray((input.size * input[0].length + 63) / 64)
    var start: Location? = null
    var end: Location? = null
    for (y in input.indices)
        for (x in input.indices)
            when (input[y][x]) {
                '#' -> setOccupied(maze, input.size, x, y)
                'S' -> start = Pair(x, y)
                'E' -> end = Pair(x, y)
            }

    println(part1(maze, input.size, start!!, end!!))
    println(part2(maze, input.size, start, end))
}

fun setOccupied(maze: LongArray, size: Int, x: Int, y: Int) {
    val index = y * size + x
    val longIndex = index / 64
    val bitIndex = index % 64
    maze[longIndex] = maze[longIndex] or (1L shl bitIndex)
}
fun isOccupied(maze: LongArray, size: Int, x: Int, y: Int): Boolean {
    val index = y * size + x
    val longIndex = index / 64
    val bitIndex = index % 64
    return maze[longIndex] and (1L shl bitIndex) != 0L
}

fun getSolution(maze: LongArray, size: Int, start: Location, end: Location): Array<Location> {
    //Stolen from day 18

    data class Node(val x: Int, val y: Int, val parent: Node?)
    val deltas = arrayOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)

    var solution: Array<Location>? = null

    val explored = mutableSetOf<Location>()
    val toExplore = ArrayDeque<Node>()
    toExplore.add(Node(start.first, start.second, null))

    fun canBeExplored(x: Int, y: Int): Boolean {
        //Out of bounds
        if (x !in 0 until size || y !in 0 until size)
            return false
        //At occupied location
        if (isOccupied(maze, size, x, y))
            return false
        //Has been explored
        if (!explored.add(Pair(x, y)))
            return false
        return true
    }

    while (toExplore.isNotEmpty()) {
        val currentNode = toExplore.removeFirst()
        val (x, y) = currentNode

        //At the goal
        if (x == end.first && y == end.second) {
            //Return the path to the goal
            val path = mutableListOf<Location>()
            var node: Node? = currentNode
            while (node != null) {
                path.add(Pair(node.x, node.y))
                node = node.parent
            }
            solution = path.toTypedArray()
            break
        }

        for (delta in deltas) {
            val newLocation = Node(x + delta.first, y + delta.second, currentNode)
            val (newX, newY) = newLocation

            if (canBeExplored(newX, newY))
                toExplore.add(newLocation)
        }
    }

    if (solution == null)
        throw RuntimeException("No solution found")

    return solution
}

fun part1(maze: LongArray, size: Int, start: Location, end: Location): Int {
    val solution = getSolution(maze, size, start, end)

    var sum = 0

    for (startX in 1 until size - 1)
        for (startY in 1 until size - 1) {
            if (isOccupied(maze, size, startX, startY))
                continue

            for (i in 0..1) {
                //Check right once and down once
                val (endX, endY) = if (i == 0) Pair(startX + 2, startY) else Pair(startX, startY + 2)

                if (endY >= size - 1 || endX >= size - 1)
                    continue
                if (isOccupied(maze, size, endX, endY))
                    continue

                //Between start and end location needs to be occupied
                if (!isOccupied(maze, size, (startX + endX) / 2, (startY + endY) / 2))
                    continue

                val startIndex = solution.indexOf(Pair(startX, startY))
                if (startIndex == -1)
                    continue
                val endIndex = solution.indexOf(Pair(endX, endY))
                if (endIndex == -1)
                    continue

                val saved = abs(endIndex - startIndex) - 2
                if (saved >= 100)
                    sum++
            }
        }

    return sum
}

fun part2(maze: LongArray, size: Int, start: Location, end: Location): Int {
    val solution = getSolution(maze, size, start, end)

    var sum = 0

    //Generate every 2-square combo (order doesn't matter) and make sure not to check one multiple times
    for ((startX, startY) in solution) {
        for (dx in 0..20)
            for (dy in -20..20) {
                if (dx == 0 && dy < 0)
                    continue

                //Make sure the distance cheated is valid
                val cheatDistance = abs(dx) + abs(dy)
                if (cheatDistance !in 2..20)
                    continue

                val endX = startX + dx
                val endY = startY + dy

                //Make sure the ending square is valid
                if (endY !in 1..<size || endX !in 1..<size)
                    continue
                if (isOccupied(maze, size, endX, endY))
                    continue

                //Get the indices of the start and end of the cheat in the solution
                val startIndex = solution.indexOf(Pair(startX, startY))
                if (startIndex == -1)
                    continue
                val endIndex = solution.indexOf(Pair(endX, endY))
                if (endIndex == -1)
                    continue

                //The distance from the start of the cheat to the end of the cheat following the solution
                //is the saved time, and the Manhattan distance between the start and end of the cheat
                //is the lost time
                val saved = abs(endIndex - startIndex) - cheatDistance
                if (saved >= 100)
                    sum++
            }
    }

    return sum
}
