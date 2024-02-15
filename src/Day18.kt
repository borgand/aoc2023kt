data class DigPlan(val rules: List<String>) {
    val ruleList = mutableListOf<Pair<Direction,Int>>()
    var path = listOf<Pair<Int, Int>>()
    fun parseRules(part2: Boolean = false) {
        var dir:Direction
        var len:Int
        for ((i, rule) in rules.withIndex()) {
            val (d, l, c) = rule.split(" ")
            if (part2) {
                val hexCount = c.substring(2, c.length - 2) // omit parenthesis, first hash sign and last digit
                // hexCount is 5 hex digits, convert to decimal
                len = hexCount.toInt(16)
                val dirNum = c.substring(c.length - 2, c.length - 1).toInt()
                dir = listOf(Direction.R, Direction.D, Direction.L, Direction.U)[dirNum]
            } else {
                dir = Direction.fromString(d)
                len = l.toInt()
            }
            ruleList.add(Pair(dir, len))
        }
        path = dirAndLenToPoints(ruleList, 0 to 0)
    }

    fun boundaryLength(): Long {
        return ruleList.fold(0L) { acc, p -> acc + p.second }
    }
    fun area(): Long {
        // Path is already counter-clockwise, so we can use the polygonArea function
        val boundaryLen = boundaryLength()
        val area = shoelaceArea(path)
        // the points are in the centre of the squares, so add half of the boundary points
        // and 1 for total sum of corners (360 degree turn adding up all corners to 1 square)
        return area + boundaryLen / 2 + 1
    }
}

fun main() {
    fun part1(input: List<String>, debug: Boolean = false): Long {
        val plan = DigPlan(input)
        plan.parseRules()
        val res = plan.area()
        return res
    }

    fun part2(input: List<String>, debug: Boolean = false): Long {
        val plan = DigPlan(input)
        plan.parseRules(true)
        val res = plan.area()
        return res
    }

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    println("Day18:")
    measureRun("Part1 Duration:") {
        val debug = true
//        val res = part1(testInput,  debug)
        val res = part1(input, debug)
        println("Part 1 Result: $res")
    }
    measureRun("Part2 Duration:") {
        val debug = false
//        val res = part2(testInput, debug)
        val res = part2(input, debug)
        println("Part2 Result: $res")
    }

}
