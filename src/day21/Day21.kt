package day21

import java.io.File
import kotlin.math.abs

fun main() {
    val instructions = File("src/day21/input.txt").readLines().toTypedArray()

    println(part1(instructions))
    println(part2(instructions))
}

const val numberKeypad = "789456123 0A"
const val directionKeypad = " ^A<v>"

fun part1(instructions: Array<String>): Long {
    fun requiredRobotInstructions(points: String, depth: Int): String {
        if (depth == 3)
            return points

        val keypad = if (depth == 0) numberKeypad else directionKeypad
        var newPoints = ""
        var x = 2
        var y = if (depth == 0) 3 else 0

        for (point in points) {
            val pointIndex = keypad.indexOf(point)
            val pointY = pointIndex / 3
            val pointX = pointIndex % 3

            val dx = pointX - x
            val dy = pointY - y

            var instruction1: String? = null
            var instruction2: String? = null

            val xMovement = String(CharArray(abs(dx)) { if (dx < 0) '<' else '>' })
            val yMovement = String(CharArray(abs(dy)) { if (dy < 0) '^' else 'v' })

            if (keypad[y * 3 + pointX] != ' ')
                instruction1 = requiredRobotInstructions(xMovement + yMovement + 'A', depth + 1)
            if (keypad[pointY * 3 + x] != ' ')
                instruction2 = requiredRobotInstructions(yMovement + xMovement + 'A', depth + 1)

            newPoints +=
                if (instruction2 == null)
                    instruction1
                else if (instruction1 == null)
                    instruction2
                else if (instruction1.length < instruction2.length)
                    instruction1
                else
                    instruction2

            x = pointX
            y = pointY
        }

        return newPoints
    }

    var sum = 0L

    //Start at the keypad on the door and add the complexities of the codes to the sum
    for (instruction in instructions) {
        //Every char in instruction is a point that needs to be reached and pressed
        val shortestSequence = requiredRobotInstructions(instruction, depth=0)
        val numericPartOfCode = instruction.takeWhile { it.isDigit() }.toInt()
        sum += shortestSequence.length * numericPartOfCode
    }

    return sum
}

fun part2(instructions: Array<String>): Long {
    val costs = mutableMapOf<Triple<Char, Char, Int>, Long>()

    fun cost(from: Char, to: Char, depth: Int): Long {
        if (depth == 26)
            return 1

        val triple = Triple(from, to, depth)
        val cost = costs[triple]
        if (cost != null)
            return cost

        val keypad = if (depth == 0) numberKeypad else directionKeypad

        val oldPointIndex = keypad.indexOf(from)
        val oldY = oldPointIndex / 3
        val oldX = oldPointIndex % 3

        val newPointIndex = keypad.indexOf(to)
        val newY = newPointIndex / 3
        val newX = newPointIndex % 3

        val dx = newX - oldX
        val dy = newY - oldY

        val xChar = if (dx < 0) '<' else '>'
        val yChar = if (dy < 0) '^' else 'v'
        val xMovement = String(CharArray(abs(dx)) { xChar })
        val yMovement = String(CharArray(abs(dy)) { yChar })

        val path1 =
            if (keypad[oldY * 3 + newX] == ' ') null
            else 'A' + xMovement + yMovement + 'A'
        val path2 =
            if (keypad[newY * 3 + oldX] != ' ')
                'A' + yMovement + xMovement + 'A'
            else null

        fun cost(path: String): Long {
            return (1..path.lastIndex).sumOf { i -> cost(path[i - 1], path[i], depth + 1) }
        }

        val lowestCost =
            if (path1 == null)
                cost(path2!!)
            else if (path2 == null)
                cost(path1)
            else
                minOf(cost(path1), cost(path2))

        costs[triple] = lowestCost
        return lowestCost
    }

    var sum = 0L

    for (instruction in instructions) {
        var codeSum = 0L
        for (i in instruction.indices) {
            val from = if (i != 0) instruction[i - 1] else 'A'
            codeSum += cost(from, instruction[i], 0)
        }
        sum += codeSum * instruction.takeWhile { it.isDigit() }.toInt()
    }

    return sum
}
