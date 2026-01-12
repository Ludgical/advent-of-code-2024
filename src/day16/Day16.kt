package day16

import java.io.File
import java.util.PriorityQueue

fun main() {
    val grid = File("src/day16/input.txt").readLines().map { it.toCharArray() }.toTypedArray()
    val size = grid.size

    var startX = -1
    var startY = -1
    var endX = -1
    var endY = -1

    for (y in 0 until size)
        for (x in 0 until size) {
            if (grid[y][x] == 'S') {
                startX = x
                startY = y
            }
            else if (grid[y][x] == 'E') {
                endX = x
                endY = y
            }
        }

    println(part1(grid, startX, startY, endX, endY))
    println(part2(grid, startX, startY, endX, endY))
}

fun part1(grid: Array<CharArray>, startX: Int, startY: Int, endX: Int, endY: Int): Int {
    data class State(val x: Int, val y: Int, val dx: Int, val dy: Int)

    val start = State(startX, startY, 1, 0)
    val shortestPaths = mutableMapOf(start to 0)
    val toExplore = PriorityQueue<Pair<State, Int>>(compareBy{ it.second })
    toExplore.add(start to 0)

    val neighborArray = arrayOfNulls<Pair<State, Int>>(3)
    fun neighbors(start: State): Array<Pair<State, Int>?> {
        val (x, y, dx, dy) = start

        val frontX = x + dx
        val frontY = y + dy
        neighborArray[0] =
            if (grid[frontY][frontX] != '#')
                State(frontX, frontY, frontX - x, frontY - y) to 1
            else null

        val side1X = x + if (dx != 0) 0 else 1
        val side1Y = y + if (dy != 0) 0 else 1
        neighborArray[1] =
            if (grid[side1Y][side1X] != '#')
                State(side1X, side1Y, side1X - x, side1Y - y) to 1001
            else null

        val side2X = x + if (dx != 0) 0 else -1
        val side2Y = y + if (dy != 0) 0 else -1
        neighborArray[2] =
            if (grid[side2Y][side2X] != '#')
                State(side2X, side2Y, side2X - x, side2Y - y) to 1001
            else null

        return neighborArray
    }

    while (toExplore.isNotEmpty()) {
        val (state, cost) = toExplore.poll()

        if (cost > shortestPaths[state]!!)
            continue

        if (state.x == endX && state.y == endY)
            return cost

        for (neighbor in neighbors(state)) {
            if (neighbor == null)
                continue
            val (newState, weight) = neighbor
            val newCost = cost + weight
            if (newCost < shortestPaths.getOrDefault(newState, Int.MAX_VALUE)) {
                shortestPaths[newState] = newCost
                toExplore.add(newState to newCost)
            }
        }
    }

    throw IllegalArgumentException("No solution found")
}

fun part2(grid: Array<CharArray>, startX: Int, startY: Int, endX: Int, endY: Int): Int {
    data class Location(val x: Int, val y: Int)
    data class State(val location: Location, val delta: Location)

    val start = State(Location(startX, startY), Location(1, 0))
    //Get location, then direction to get (distance from start, tiles that are part of one of the best paths there)
    val stateInfo = mutableMapOf(start.location to mutableMapOf(start.delta to Pair(0, mutableSetOf<Location>())))

    //States to explore sorted by the distance from the start
    val toExplore = PriorityQueue<Pair<State, Int>>(compareBy{ it.second })
    toExplore.add(start to 0)

    //Get the neighbors of a state and the costs to get there
    val neighborArray = arrayOfNulls<Pair<State, Int>>(3)
    fun neighbors(start: State): Array<Pair<State, Int>?> {
        val (x, y) = start.location
        val (dx, dy) = start.delta

        val frontX = x + dx
        val frontY = y + dy
        neighborArray[0] =
            if (grid[frontY][frontX] != '#')
                State(Location(frontX, frontY), Location(frontX - x, frontY - y)) to 1
            else null

        val side1X = x + if (dx != 0) 0 else 1
        val side1Y = y + if (dy != 0) 0 else 1
        neighborArray[1] =
            if (grid[side1Y][side1X] != '#')
                State(Location(side1X, side1Y), Location(side1X - x, side1Y - y)) to 1001
            else null

        val side2X = x + if (dx != 0) 0 else -1
        val side2Y = y + if (dy != 0) 0 else -1
        neighborArray[2] =
            if (grid[side2Y][side2X] != '#')
                State(Location(side2X, side2Y), Location(side2X - x, side2Y - y)) to 1001
            else null

        return neighborArray
    }

    //Put all the values in all the sets in the first set in the list
    fun mergeAll(locationsList: List<MutableSet<Location>>): MutableSet<Location> {
        for (i in 1..locationsList.lastIndex)
            locationsList[0].addAll(locationsList[i])
        return locationsList[0]
    }

    var distToGoal: Int? = null

    while (toExplore.isNotEmpty()) {
        //Get the state with the lowest cost
        val (state, cost) = toExplore.poll()
        val (x, y) = state.location

        //If the state is at the goal and the distance to the goal is not set, set it
        if (x == endX && y == endY && distToGoal == null)
            distToGoal = cost

        //When no more states can reach the goal, return the amount of squares that are on an optimal path to the goal
        if (distToGoal != null && cost > distToGoal) {
            val squaresOnOptimalPath = stateInfo[Location(endX, endY)]!!.values.map { it.second }
            return mergeAll(squaresOnOptimalPath).size + 1
        }

        val currentStateInfo = stateInfo[state.location]!![state.delta]!!

        //If it costs more to get to a state from the new path than an already found path, continue
        if (cost > currentStateInfo.first)
            continue

        for (neighbor in neighbors(state)) {
            if (neighbor == null)
                continue

            val (newState, weight) = neighbor
            val newCost = cost + weight

            val newStateDeltas = stateInfo.getOrPut(newState.location) { mutableMapOf() }

            //Get the cost of the previous path to get to the state
            val previousCost = (newStateDeltas[newState.delta]?: Pair(Int.MAX_VALUE, null)).first

            //If the cost of the path to the new state and the previous cost of the path to the new state are the same,
            //merge the squares in the paths to the state
            if (newCost == previousCost)
                newStateDeltas[newState.delta]!!.second.addAll(currentStateInfo.second)

            //If the new cost is better than the previous cost,
            //add the location of the state to the squares on one of the optimal paths,
            //save the state to stateInfo (newStateDeltas) and add it to toExplore
            if (newCost < previousCost) {
                val squaresOnOptimalPath = currentStateInfo.second.toMutableSet()
                squaresOnOptimalPath.add(state.location)
                newStateDeltas[newState.delta] = Pair(newCost, squaresOnOptimalPath)
                toExplore.add(newState to newCost)
            }
        }
    }

    throw IllegalArgumentException("No solution found")
}