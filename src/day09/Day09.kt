package day09

import java.io.File

fun main() {
    val input = File("src/day09/input.txt").readText()

    val disk = mutableListOf<Int>()
    for (i in input.indices) {
        if (i % 2 == 0)
            repeat (input[i].digitToInt()) {
                disk.add(i shr 1)
            }
        else
            repeat (input[i].digitToInt()) {
                disk.add(-1)
            }
    }

    val diskArray = disk.toIntArray()

    println(part1(diskArray))
    println(part2(diskArray))
}

fun part1(disk: IntArray): Long {
    var sum = 0L
    var i = 0
    var last = disk.lastIndex

    while (i <= last) {
        var num = disk[i]

        if (num == -1) {
            while (disk[last] == -1)
                last--
            num = disk[last]
            last--
        }

        sum += i * num
        i++
    }

    return sum
}

fun part2(disk: IntArray): Long {
    var sum = 0L

    var i = disk.lastIndex
    while (i >= 0) {
        val id = disk[i]
        var size = 1
        i--
        if (i != -1)
            while (disk[i] == id) {
                size++
                i--
                if (i == -1)
                    break
            }

        var j = 0
        var spaceAmount = 0
        while (spaceAmount < size && j <= i) {
            if (disk[j] == -1)
                spaceAmount++
            else
                spaceAmount = 0
            j++
        }

        if (spaceAmount == size) {
            val oldLoc = i + 1
            val newLoc = j - size
            for (offset in 0 until size) {
                disk[oldLoc + offset] = -1
                disk[newLoc + offset] = id
            }
        }
    }

    for (index in disk.indices) {
        if (disk[index] == -1)
            continue
        sum += disk[index] * index
    }

    return sum
}