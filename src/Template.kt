

fun main(){
    fun part1(input:List<String>, debug:Boolean = false):Int{
        return input.size
    }

    fun part2(input:List<String>, debug:Boolean = false):Int{
        return input.size
    }

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    println("Day8:")
    measureRun("Part1") {
        val debug = false
        var res = part1(testInput,  debug)
//        var res = part1(input, true, debug)
        println("Result: $res")
    }
    measureRun("Part2") {
        val debug = false
        var res = part2(testInput,  debug)
//        var res = part1(input, true, debug)
        println("Result: $res")
    }

}
