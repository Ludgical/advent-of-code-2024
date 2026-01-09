package day14

import java.io.File

fun main() {
    val input = File("src/day14/input.txt").readLines()

    val robots = input.map { line ->
        val (position, velocity) = line.split(" ").map { it.takeLast(it.length - 2).split(",").map { pair -> pair.toInt() } }
        Pair(
            Pair(position[0], position[1]),
            Pair(velocity[0], velocity[1])
        )
    }.toTypedArray()

    println(part1(robots))
    println(part2(robots))
}

fun part1(robots: Array<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Int {
    val newLocations = robots.map {
        val newX = (it.first.first  + (it.second.first  * 100)) % 101
        val newY = (it.first.second + (it.second.second * 100)) % 103
        Pair(
            newX + if (newX < 0) 101 else 0,
            newY + if (newY < 0) 103 else 0
        )
    }.toTypedArray()

    var topLeft = 0
    var topRight = 0
    var bottomLeft = 0
    var bottomRight = 0
    for (location in newLocations) {
        if (location.first < 50) {
            if (location.second < 51)
                topLeft++
            else if (location.second > 51)
                bottomLeft++
        }
        else if (location.first > 50) {
            if (location.second < 51)
                topRight++
            else if (location.second > 51)
                bottomRight++
        }
    }

    return topLeft * topRight * bottomLeft * bottomRight
}

data class Location(var x: Int, var y: Int)

fun part2(robots: Array<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Int {
    //Make it easier to access the locations and velocities
    val locations  = robots.map { Location(it.first.first,  it.first.second)  }.toTypedArray()
    val velocities = robots.map { Location(it.second.first, it.second.second) }.toTypedArray()

    //Create a bit board to store which squares have robots
    val bitBoard = LongArray((101 * 103 + 63) / 64)
    //To help write to the bit board
    val bitIndexMask = 0b111111
    val longIndexMask = bitIndexMask.inv()

    //Start at move 1 and check every move until a Christmas tree is found
    var moveIndex = 1
    while (true) {
        //Go over every robot location
        for (index in locations.indices) {
            val location = locations[index]
            val velocity = velocities[index]

            //Move it by the velocity and make sure it is on the grid
            location.x += velocity.x
            while (location.x >= 101)
                location.x -= 101
            while (location.x < 0)
                location.x += 101
            location.y += velocity.y
            while (location.y >= 103)
                location.y -= 103
            while (location.y < 0)
                location.y += 103

            //Save the location in the bit board
            //The index of the bit
            val index = location.y * 101 + location.x
            //The long index is bits at 64-bit and up (0..array size - 1)
            val longIndex = (index and longIndexMask) shr 6
            //The bit index is bits at 32-bit and down (0..63)
            val bitIndex = index and bitIndexMask
            bitBoard[longIndex] = bitBoard[longIndex] or (1L shl bitIndex)
        }

        //There is always a 31 bit long line in the Christmas tree
        //Check the 4 16-bit groups in the long
        //If there are 16 or 15 set bits, it should be a Christmas tree
        for (long in bitBoard) {
            var y = 0
            while (y <= 48) {
                if ((long and (0b1111_1111_1111_1111L shl y)).countOneBits() >= 15)
                    return moveIndex
                y += 16
            }
        }

        //Clear the bit board
        for (index in bitBoard.indices)
            bitBoard[index] = 0

        moveIndex++
    }
}