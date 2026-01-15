package day18

import java.io.File

fun main() {
    val coordinates = File("src/day18/input.txt").readLines().map { line ->
        val strCoordinate = line.split(",")
        Pair(strCoordinate[0].toInt(), strCoordinate[1].toInt())
    }.toTypedArray()

    println(part1(coordinates))
    println(part2(coordinates))
}

val deltas = arrayOf(1 to 0, 0 to 1, -1 to 0, 0 to -1)

fun canPlace(x: Int, y: Int, memory: Array<BooleanArray>, explored: MutableSet<Pair<Int, Int>>): Boolean {
    //Out of bounds
    if (x !in 0..70 || y !in 0..70)
        return false
    //At occupied location
    if (memory[x][y])
        return false
    //Has been explored
    if (!explored.add(Pair(x, y)))
        return false
    return true
}

fun part1(coordinates: Array<Pair<Int, Int>>): Int {
    val memory = Array(71) { BooleanArray(71) { false } }
    for (i in 0 until 1024) {
        val (x, y) = coordinates[i]
        memory[x][y] = true
    }

    val explored = mutableSetOf<Pair<Int, Int>>()
    val toExplore = ArrayDeque<Triple<Int, Int, Int>>()
    toExplore.add(Triple(0, 0, 0))

    while (toExplore.isNotEmpty()) {
        val (x, y, depth) = toExplore.removeFirst()

        //At the goal
        if (x == memory.lastIndex && y == memory.lastIndex)
            return depth

        for (delta in deltas) {
            val newLocation = Triple(x + delta.first, y + delta.second, depth + 1)
            val (newX, newY) = newLocation

            //canPlace also adds (newX, newY) to explored
            if (canPlace(newX, newY, memory, explored))
                toExplore.add(newLocation)
        }
    }

    return -1
}

fun part2(coordinates: Array<Pair<Int, Int>>): String {
    data class Node(val x: Int, val y: Int, val depth: Int, val parent: Node?)

    //Create the empty board and set the first 1024 locations to true
    val memory = Array(71) { BooleanArray(71) { false } }
    for (i in 0 until 1024) {
        val (x, y) = coordinates[i]
        memory[x][y] = true
    }

    fun getFastestPath(): Array<Node>? {
        val explored = mutableSetOf<Pair<Int, Int>>()
        val toExplore = ArrayDeque<Node>()
        toExplore.add(Node(0, 0, 0, null))

        while (toExplore.isNotEmpty()) {
            val currentNode = toExplore.removeFirst()
            val (x, y, depth) = currentNode

            //At the goal
            if (x == memory.lastIndex && y == memory.lastIndex) {
                //Return the path to the goal
                val path = mutableListOf<Node>()
                var node: Node? = currentNode
                while (node != null) {
                    path.add(node)
                    node = node.parent
                }
                return path.toTypedArray()
            }

            for (delta in deltas) {
                val newLocation = Node(x + delta.first, y + delta.second, depth + 1, currentNode)
                val (newX, newY) = newLocation

                if (canPlace(newX, newY, memory, explored))
                    toExplore.add(newLocation)
            }
        }

        return null
    }

    var fastestPath = getFastestPath()

    fun fastestPathWorks(): Boolean {
        for (node in fastestPath!!) {
            val (x, y) = node
            if (memory[x][y])
                return false
        }
        return true
    }

    //Go over every coordinate that hasn't been placed
    for (index in 1024 until coordinates.size) {
        val (x, y) = coordinates[index]
        memory[x][y] = true

        //Check if the previous fastest path still works
        if (fastestPathWorks())
            continue

        //If it doesn't work, get the fastest path in the new memory
        fastestPath = getFastestPath()

        //If it returns null, there is no solution
        if (fastestPath == null)
            return "$x,$y"
    }

    return "?"
}
