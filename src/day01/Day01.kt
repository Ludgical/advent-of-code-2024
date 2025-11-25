package day01

import java.io.File
import kotlin.math.abs

fun main() {
    val input = input("01", "input")
    println(part1(input[0], input[1]))
    println(part2(input[0], input[1]))
}

fun input(day: String, type: String): Array<MutableList<Int>> {
    val input = File("src/day$day/$type.txt").readLines()
    val lists = Array(2) { mutableListOf<Int>() }
    for(line in input) {
        val nums = line.split("   ")
        lists[0].add(nums[0].toInt())
        lists[1].add(nums[1].toInt())
    }

    return lists
}

fun part1(leftList: MutableList<Int>, rightList: MutableList<Int>): Int {
    leftList.sort()
    rightList.sort()

    var sum = 0
    for(i in leftList.indices) {
        sum += abs(leftList[i] - rightList[i])
    }

    return sum
}
fun part2(leftList: MutableList<Int>, rightList: MutableList<Int>): Int {
    var sum = 0
    for(num in leftList) {
        sum += num * rightList.count { it == num }
    }
    return sum
}