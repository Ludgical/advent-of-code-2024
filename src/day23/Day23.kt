package day23

import java.io.File

data class Computer(val c1: Char, val c2: Char) {
    override fun toString() = "${this.c1}${this.c2}"
}

fun main() {
    val connections = File("src/day23/input.txt").readLines().map {
        val (computer1, computer2) = it.split("-")
        Pair(Computer(computer1[0], computer1[1]), Computer(computer2[0], computer2[1]))
    }.toTypedArray()

    val connectionMap = mutableMapOf<Computer, MutableList<Computer>>()

    for ((computer1, computer2) in connections) {
        connectionMap.getOrPut(computer1) { mutableListOf() }.add(computer2)
        connectionMap.getOrPut(computer2) { mutableListOf() }.add(computer1)
    }

    println(part1(connectionMap))
    println(part2(connectionMap.map { it.key to it.value.toSet() }.toMap()))
}

fun part1(connectionMap: Map<Computer, List<Computer>>): Int {
    val tripleConnections = mutableSetOf<Triple<Computer, Computer, Computer>>()

    for ((computer1, computers) in connectionMap) {

        for (i in 0 until computers.size - 1) {
            val computer2 = computers[i]

            for (j in i + 1 until computers.size) {
                val computer3 = computers[j]

                if (computer1.c1 != 't' && computer2.c1 != 't' && computer3.c1 != 't')
                    continue

                if (connectionMap[computer2]!!.contains(computer3)) {
                    val triple = arrayOf(computer1, computer2, computer3)
                    triple.sortBy { (it.c1.code shl 8) + it.c2.code }
                    tripleConnections.add(Triple(triple[0], triple[1], triple[2]))
                }
            }
        }
    }

    return tripleConnections.size
}

fun part2(graph: Map<Computer, Set<Computer>>): String {
    var maxClique = emptySet<Computer>()

    fun bronKerbosch(current: Set<Computer>, canBeAdded: MutableSet<Computer>, considered: MutableSet<Computer>) {
        if (canBeAdded.isEmpty() && considered.isEmpty()) {
            if (current.size > maxClique.size)
                maxClique = current.toSet()
            return
        }

        val pivot = (canBeAdded + considered).firstOrNull()
        val candidates = canBeAdded - (graph[pivot]?: emptySet())

        for (computer in candidates) {
            val newCurrent = current.plus(computer)

            val neighbours = graph[computer]!!
            val newCanBeAdded = canBeAdded.intersect(neighbours).toMutableSet()
            val newConsidered = considered.intersect(neighbours).toMutableSet()

            bronKerbosch(newCurrent, newCanBeAdded, newConsidered)

            canBeAdded.remove(computer)
            considered.add(computer)
        }
    }

    bronKerbosch(setOf(), graph.keys.toMutableSet(), mutableSetOf())
    return maxClique.sortedBy{ (it.c1.code shl 8) + it.c2.code }.joinToString(",")
}
