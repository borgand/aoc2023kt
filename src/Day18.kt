data class Rule(val dir:Direction, val len:Int, val colour:String)
data class DigPlan(val rules:List<String>){
    val ruleList = mutableListOf<Rule>()
    val dirList = mutableListOf<Direction>()
    var path:List<Pair<Int,Int>> = listOf(0 to 0)
    var innerTiles = mutableSetOf<Pair<Int,Int>>()
    var tileCount = 0



    fun parseRules(){
        for(rule in rules){
            val (d,l,c) = rule.split(" ")
            val dir = Direction.fromString(d)
            val len = l.toInt()
            // separate colorcode from parenthesis
            val colour = c.substring(1,c.length-1)
            // create unfoldd list of directions
            dirList.addAll(List(len){dir})
            ruleList.add(Rule(dir,len,colour))
        }
        path = dirListToPath(dirList)
    }

    fun count():Int{
        var iterations = 0
        fun isWall(pos:Pair<Int,Int>):Boolean{
            return pos in path
        }

        fun visit(pos:Pair<Int,Int>){
            innerTiles.add(pos)
            iterations++
//            if (iterations % 100 == 0) {
//                draw(true)
//            }
        }
        val startPos = firstInnerTile(path, dirList.first())
        if(startPos != null) {
            floodFill(1 to 1, ::isWall, ::visit)
            tileCount = innerTiles.size + path.size - 1 // start pos is double counted
        }
        return  tileCount
    }

    fun draw(clear:Boolean = false){
        // position cursor at top left if clear is true
        if(clear){
            print("\u001B[H\u001B[2J")
        }
        val maxX = path.maxByOrNull { it.first }!!.first
        val maxY = path.maxByOrNull { it.second }!!.second
        val minX = path.minByOrNull { it.first }!!.first
        val minY = path.minByOrNull { it.second }!!.second
        for(y in minY..maxY){
            for(x in minX..maxX){
                val pos = x to y
                when (pos) {
                    in path -> {
                        print("#")
                    }
                    in innerTiles -> {
                        // print in yellow color X
                        print("\u001B[33mX\u001B[0m")
                    }
                    else -> {
                        print(".")
                    }
                }
            }
            println()
        }
    }

}

fun main(){
    fun part1(input:List<String>, debug:Boolean = false):Int{
        val plan = DigPlan(input)
        plan.parseRules()
        plan.count()
        plan.draw()
        return plan.tileCount
    }

    fun part2(input:List<String>, debug:Boolean = false):Int{
        return input.size
    }

    val testInput = readInput("Day18_test")
    val input = readInput("Day18")

    println("Day8:")
    measureRun("Part1") {
        val debug = false
//        var res = part1(testInput,  debug)
        var res = part1(input, debug)
        println("Result: $res")
    }
    measureRun("Part2") {
        val debug = false
        var res = part2(testInput,  debug)
//        var res = part1(input, debug)
        println("Result: $res")
    }

}
