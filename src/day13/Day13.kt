package day13

import java.io.File
import java.lang.Long.min

data class Machine (
    val aIncrement: Pair<Int, Int>,
    val bIncrement: Pair<Int, Int>,
    var prize: Pair<Long, Long>
)

fun main() {
    val input = File("src/day13/input.txt").readText().split("\n\n")

    val machines = Array(input.size) { i ->
        val (buttonA, buttonB, prize) = input[i].split("\n")
        
        val aXInc = buttonA.substring(buttonA.indexOf('X') + 2, buttonA.indexOf(',')).toInt()
        val aYInc = buttonA.substring(buttonA.indexOf('Y') + 2).toInt()
        val bXInc = buttonB.substring(buttonB.indexOf('X') + 2, buttonB.indexOf(',')).toInt()
        val bYInc = buttonB.substring(buttonB.indexOf('Y') + 2).toInt()
        val prizeX = prize.substring(prize.indexOf('X') + 2, prize.indexOf(',')).toLong()
        val prizeY = prize.substring(prize.indexOf('Y') + 2).toLong()

        Machine(Pair(aXInc, aYInc), Pair(bXInc, bYInc), Pair(prizeX, prizeY))
    }

    println(part1(machines))
    println(part2(machines))
}

fun part1(machines: Array<Machine>): Long {
    var sum = 0L

    for (machine in machines) {
        val (aInc, bInc, prize) = machine

        var minCost = Long.MAX_VALUE

        val maxAPresses = min(prize.first / aInc.first, prize.second / aInc.second)
        for (aPresses in 0..min(maxAPresses, 100)) {
            val x = aPresses * aInc.first
            val y = aPresses * aInc.second
            val requiredX = prize.first - x
            val requiredY = prize.second - y

            if (requiredX < 0 || requiredY < 0)
                break

            val bPressesRequiredX = requiredX.toDouble() / bInc.first
            val bPressesRequiredY = requiredY.toDouble() / bInc.second

            if (bPressesRequiredX == bPressesRequiredY && bPressesRequiredX % 1 == 0.0 && bPressesRequiredY % 1 == 0.0)
                minCost = min(minCost, aPresses * 3 + bPressesRequiredX.toInt())
        }

        if (minCost != Long.MAX_VALUE)
            sum += minCost
    }

    return sum
}

fun part2(machines: Array<Machine>): Long {
    for (machine in machines) {
        machine.prize = Pair(machine.prize.first + 10000000000000, machine.prize.second + 10000000000000)
    }

    var sum = 0L

    for (machine in machines) {
        //Using ChatGPT and Cramer's rule

        //swapped r and q because I understand Cramer's rule now
        val (p, r) = machine.aIncrement
        val (q, s) = machine.bIncrement
        val (n, m) = machine.prize

        val d = p * s - r * q

        if (d == 0)
            throw IllegalArgumentException("d == 0")

        val aNum = n * s - q * m
        val bNum = p * m - n * r

        if (aNum % d != 0L || bNum % d != 0L)
            continue

        val aPresses = aNum / d
        val bPresses = bNum / d

        sum += 3 * aPresses + bPresses
    }

    return sum
}