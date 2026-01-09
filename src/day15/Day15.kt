package day15

import java.io.File

data class Location(var y: Int, var x: Int)

fun main() {
    val input = File("src/day15/input.txt").readText()

    val (gridStr, movesStr) = input.split("\n\n")
    val grid1 = gridStr.split("\n").map { it.toCharArray() }.toTypedArray()
    val moves = movesStr.replace("\n", "").toCharArray()
    var start = Location(-1, -1)

    yLoop@
    for (y in grid1.indices)
        for (x in grid1[y].indices)
            if (grid1[y][x] == '@') {
                start = Location(y, x)
                break@yLoop
            }

    println(part1(grid1, Location(start.x, start.y), moves))

    val grid2 = gridStr.replace("#", "##").replace("O", "[]").replace(".", "..").replace("@", "@.")
        .split("\n").map { it.toCharArray() }.toTypedArray()
    start.x *= 2

    println(part2(grid2, start, moves))
}

fun part1(grid: Array<CharArray>, location: Location, moves: CharArray): Int {
    for (move in moves) {
        val (dx, dy) = when (move) {
            '^' -> Location(0, -1)
            '<' -> Location(-1, 0)
            '>' -> Location(1, 0)
            else -> Location(0, 1)
        }

        fun pushIfPossible(grid: Array<CharArray>, x: Int, y: Int, dx: Int, dy: Int): Boolean {
            if (grid[y][x] == '.')
                return true
            if (grid[y][x] == '#')
                return false

            val possible = pushIfPossible(grid, x + dx, y + dy, dx, dy)
            if (possible) {
                grid[y + dy][x + dx] = grid[y][x]
                grid[y][x] = '.'
                return true
            }
            return false
        }

        val possible = pushIfPossible(grid, location.x, location.y, dx, dy)
        if (possible) {
            location.x += dx
            location.y += dy
        }
    }

    var sum = 0

    for (y in grid.indices)
        for (x in grid[y].indices)
            if (grid[y][x] == 'O')
                sum += y * 100 + x

    return sum
}

fun part2(grid: Array<CharArray>, location: Location, moves: CharArray): Int {
    for (move in moves) {
        //Keep track of explored paths so we don't explore them again
        val explored = mutableSetOf<Pair<Int, Int>>()
        //Keep track of locations to push
        val toPush = mutableSetOf<Pair<Int, Int>>()

        fun pushIfPossibleY(grid: Array<CharArray>, x: Int, y: Int, dy: Int): Boolean {
            //Return true if the path has been or is being explored
            if (explored.contains(Pair(y, x)))
                return true

            explored.add(Pair(y, x))

            val type = grid[y][x]

            //You can push the location before this location
            if (type == '.')
                return true
            //You can't push the location before this location
            if (type == '#')
                return false

            if (type == '@') {
                //Explore the location in front of the robot
                val possible = pushIfPossibleY(grid, x, y + dy, dy)
                if (possible) {
                    toPush.add(Pair(y, x))
                    return true
                }
            }

            else if (type == '[') {
                //Make sure both parts of the box can be pushed
                val possible =
                    pushIfPossibleY(grid, x, y + dy, dy) &&
                    pushIfPossibleY(grid, x + 1, y, dy)
                if (possible) {
                    toPush.add(Pair(y, x))
                    //Other part gets added in pushIfPossibleY(grid, x + 1, y, dy)
                    return true
                }
            }

            else if (type == ']') {
                //Same here
                val possible =
                    pushIfPossibleY(grid, x, y + dy, dy) &&
                    pushIfPossibleY(grid, x - 1, y, dy)
                if (possible) {
                    toPush.add(Pair(y, x))
                    return true
                }
            }

            return false
        }

        fun pushIfPossibleX(grid: Array<CharArray>, x: Int, y: Int, dx: Int): Boolean {
            //Same as part 1
            val type = grid[y][x]

            if (type == '.')
                return true
            if (type == '#')
                return false

            val possible = pushIfPossibleX(grid, x + dx, y, dx)
            if (possible) {
                toPush.add(Pair(y, x))
                return true
            }
            return false
        }

        //Get the dx and dy of the move
        val (dx, dy) = when (move) {
            '^' -> Location(0, -1)
            '<' -> Location(-1, 0)
            '>' -> Location(1, 0)
            else -> Location(0, 1)
        }

        //If dy is 0, the robot wants to move in the x-axis, otherwise it wants to move in the y-axis
        val possible =
            if (dy == 0)
                pushIfPossibleX(grid, location.x, location.y, dx)
            else
                pushIfPossibleY(grid, location.x, location.y, dy)

        //Move the robot and the boxes it would push if it can move
        if (possible) {
            for ((y, x) in toPush) {
                grid[y + dy][x + dx] = grid[y][x]
                grid[y][x] = '.'
            }
            //Change the location of the robot
            location.x += dx
            location.y += dy
        }

        toPush.clear()
    }

    var sum = 0

    for (y in grid.indices)
        for (x in grid[y].indices)
            if (grid[y][x] == '[')
                sum += y * 100 + x

    return sum
}