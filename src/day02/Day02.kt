package day02

import java.io.File
import kotlin.math.abs

fun main() {
    val input = input("02", "input")
    println(part1(input))
    println(part2(input))
}

fun input(day: String, type: String): MutableList<Array<Int>> {
    val input = File("src/day$day/$type.txt").readLines()
    val reports = mutableListOf<Array<Int>>()
    for(line in input) {
        val reportStr = line.split(" ")
        reports.add(Array(reportStr.size) { i -> reportStr[i].toInt() })
    }

    return reports
}

fun isSafe(report: MutableList<Int>): Boolean {
    var incSafeAmount = true
    var lastWasIncrease: Boolean? = null
    var prevNum = report[0]
    for (i in 1 until report.size) {
        val num = report[i]

        val diff = abs(prevNum - num)
        if(diff !in 1..3) {
            incSafeAmount = false
            break
        }

        val isIncrease = num > prevNum
        if(lastWasIncrease == null) {
            lastWasIncrease = isIncrease
        }
        else if(isIncrease != lastWasIncrease) {
            incSafeAmount = false
            break
        }

        prevNum = num
    }
    return incSafeAmount
}

fun part1(reports: MutableList<Array<Int>>): Int {
    var safeAmount = 0
    for (report in reports)
        if(isSafe(report.toMutableList()))
            safeAmount++
    return safeAmount
}

fun part2(reports: MutableList<Array<Int>>): Int {
    var safeAmount = 0
    for (report in reports)
        for(index in report.indices) {
            val newReport = report.copyOf().toMutableList()
            newReport.removeAt(index)
            if(isSafe(newReport)) {
                safeAmount++
                break
            }
        }

    return safeAmount
}