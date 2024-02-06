import java.util.PriorityQueue

enum class Direction(val step: Pair<Int, Int>){
    U( 0 to -1),
    D(0 to 1),
    L(-1 to 0),
    R(1 to 0);

    fun opposite():Direction{
        return when(this){
            U -> D
            D -> U
            L -> R
            R -> L
        }
    }
    fun move(pos: Pair<Int,Int>):Pair<Int,Int>{
        return Pair(pos.first + step.first, pos.second + step.second)
    }
}

class Node{
    val pos:Pair<Int,Int>
    val path:List<Direction>
    val heatLoss:Int
    val totalHeatLoss:Int
    val map:TownMap
    val heuristics:Int
    constructor(pos:Pair<Int,Int>, path:List<Direction>, heatLoss:Int, totalHeatLoss:Int, map:TownMap){
        this.pos = pos
        this.path = path
        this.heatLoss = heatLoss
        this.totalHeatLoss = totalHeatLoss
        this.map = map
        this.heuristics = heuristics()
    }

    fun move(dir:Direction):Node{
        val nextPos = dir.move(pos)
        val nextHeatLoss = totalHeatLoss + map.getHeatLoss(nextPos)
        return Node(nextPos, path + dir, map.getHeatLoss(nextPos), nextHeatLoss, map)
    }

    fun lastSteps(steps:Int):List<Direction>{
        return path.takeLast(steps)
    }

    fun sameStepsTaken(steps:Int):Boolean{
        val lastSteps = lastSteps(steps)
        val allTheSame = lastSteps.distinct().size == 1
        return allTheSame && lastSteps.size == steps
    }

    fun maxStepsTaken():Boolean{
        return sameStepsTaken(map.maxSteps)
    }

    fun minStepsTaken():Boolean{
        return sameStepsTaken(map.minSteps)
    }

    fun heuristics():Int{
        return totalHeatLoss + (map.width() - pos.first) + (map.height() - pos.second)
    }

    fun getDirections():List<Direction>{
        val directions = Direction.entries.toMutableList()
        val dir = path.last()

        // if min steps not taken, keep same direction
        if (!minStepsTaken())
            return listOf(dir)

        if(maxStepsTaken())
            directions.remove(dir)

        // prevent going back
        directions.remove(dir.opposite())

        // prevent going out of boundaries
        if(pos.first < map.minSteps)
            directions.remove(Direction.L)
        if(pos.first >= map.width() - map.minSteps)
            directions.remove(Direction.R)
        if(pos.second < map.minSteps)
            directions.remove(Direction.U)
        if(pos.second >= map.height() - map.minSteps)
            directions.remove(Direction.D)

        return directions
    }

    fun key():Int{
        return "${pos.first},${pos.second},${path.last()},${lastSteps(map.maxSteps)}".hashCode()
    }

    override fun hashCode(): Int {
        return key().hashCode()
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (key() != other.key()) return false

        return true
    }
}
class TownMap{
    val map = mutableMapOf<Pair<Int,Int>,Int>()
    var hideMap:Boolean = false
    var debug:Boolean = false
    // the rules on the steps the lava crucible can take
    var minSteps:Int = 1
    var maxSteps:Int = 3


    val MutableMap<Pair<Int, Int>, Int>.width: Int
        get() = (keys.maxByOrNull { it.first }?.first ?: 0) + 1

    val MutableMap<Pair<Int, Int>, Int>.height: Int
        get() = (keys.maxByOrNull { it.second }?.second ?: 0) + 1
    fun get(x:Int,y:Int):Int{
        return map.getOrDefault(Pair(x,y),0)
    }
    fun set(x:Int,y:Int,value:Int){
        map[Pair(x,y)] = value
    }

    fun width():Int{
        return map.width
    }

    fun height():Int{
        return map.height
    }

    fun parseMap(input:List<String>){
        input.forEachIndexed{y,line ->
            val digits = line.map { c -> c.digitToInt()}
            digits.forEachIndexed{x,d ->
                set(x,y,d)
            }
        }
    }
    fun getHeatLoss(pos:Pair<Int,Int>):Int{
        return get(pos.first, pos.second)
    }

    fun visitedForPos(pos:Pair<Int,Int>, visited:MutableSet<Node>):Node?{
        // filter visited by pos and return the lowest totalHeatloss node
        return visited.filter { it.pos == pos }.minByOrNull { it.totalHeatLoss }
    }

    // a function to print the whole map, coloring background of:
    // - visited nodes green
    // - path nodes red
    // - queue nodes yellow
    fun printMap(visited:MutableSet<Node>, queue:PriorityQueue<Node>, path:List<Direction>?, delay:Long=0, clear:Boolean = true, highlightPositions:List<Pair<Int,Int>>? = null){
        if (path == null)
            return
        val width = map.width
        val height = map.height
        val pathSet = path.scan(0 to 0) {pos, dir -> dir.move(pos) }.toSet()
        val queueSet = queue.map{it.pos}.toSet()
        val visitedPoss = visited.map { it.pos }.toSet()
        if (clear) {
//            print("\u001B[H\u001B[2J")
            print("\u001b[H")
            System.out.flush()
        }
        println()
        for(y in 0 until height){
            for(x in 0 until width){
                val pos = x to y
                val value = get(x,y)
                val isVisited = visitedPoss.contains(pos)
                val isPath = pathSet.contains(pos)
                val isQueue = queueSet.contains(pos)
                val isHighlight = highlightPositions?.contains(pos) ?: false
                val block = when (value) {
                    in 0..2 -> '░'
                    in 3..5 -> '▒'
                    in 6..8 -> '▓'
                    else -> '█'
                }
                val color = when{
                    isHighlight -> "\u001B[44m\u001B[30m"
                    isPath -> "\u001B[41m" // Red bg
                    isQueue -> "\u001B[43m\u001B[30m" // Yellow background and black text
                    isVisited -> "\u001B[42m\u001B[30m" // Green background and black text
                    else -> ""
                }
                if (hideMap)
                    print("$color$block\u001B[0m")
                else
                    print("$color$value\u001B[0m")
            }
            println()
        }
        Thread.sleep(delay)
    }

    fun debugPos(pos:Pair<Int,Int>, visited:MutableSet<Node>? = null){
        if (visited == null)
            return
        val vd = visitedForPos(pos, visited)
        printMap(visited, PriorityQueue<Node>(), vd?.path, 0, false, listOf(pos))
        println("pos: $pos, totalHeatLoss: ${vd?.totalHeatLoss}, path: ${vd?.path}")
    }


    fun solve(start:Pair<Int, Int>, end:Pair<Int,Int>):Int {
        val visited = mutableSetOf<Node>()
        val totalHeatlosses = mutableMapOf<Pair<Int,Int>,Int>()
        val pathMap = mutableMapOf<Pair<Int,Int>,List<Direction>>()
        val queue = PriorityQueue<Node>(compareBy { it.heuristics})
        // for better performance use HashSet to keep record of nodes in PriorityQueue
        val hashQueue = mutableSetOf<Int>()
        // initialize for start position
        totalHeatlosses[start] = getHeatLoss(start) // this is already first step, so heatloss counts
        var startNode = Node(Direction.R.move(start), listOf(Direction.R), getHeatLoss(start), getHeatLoss(start), this)
        queue.add(startNode)
        startNode = Node(Direction.D.move(start), listOf(Direction.R), getHeatLoss(start), getHeatLoss(start), this)
        queue.add(startNode)
        var count = 0
        val skipFrames = if (map.width > 20) 5000 else 10
        val delay = 20L

        while(queue.isNotEmpty()){
            count++
            val current = queue.poll()
            hashQueue.remove(current.key())
            val currentHeatLoss = current.totalHeatLoss
            val path = current.path
            pathMap[current.pos] = path
            val directions = current.getDirections()
            for(d in directions){
                val next = current.move(d)
                // if current path is greater
                if (totalHeatlosses[next.pos] == null || next.totalHeatLoss < totalHeatlosses[next.pos]!!){
                    totalHeatlosses[next.pos] = next.totalHeatLoss
                }
                if(!visited.contains(next) && !hashQueue.contains(next.key())) {
                    queue.offer(next)
                    hashQueue.add(next.key())
                }
            }
            visited.add(current)
            if (count % skipFrames == 0 || queue.isEmpty())
                if (debug)
                    printMap(visited, queue, path, delay)
        }

        val finalHeatLoss = totalHeatlosses[end] ?: 0
        val visitedEnd = visitedForPos(end, visited)
        if (visitedEnd != null) {
            printMap(visited, queue, visitedEnd.path, delay, true, listOf(end))
            "moves: $count path: ${visitedEnd.path}".printIt()
        }
        return finalHeatLoss
    }

    fun minimalHeatLoss():Int{
        val dir = Direction.R
        val start = 0 to 0
        val end = map.width - 1 to map.height - 1

        val minHeatloss = solve(start, end)


        return minHeatloss
    }
}
fun main() {
    fun part1(input: List<String>, hideMap:Boolean = false, debug:Boolean = false): Int {
        print("\u001B[H\u001B[2J")
        val map = TownMap()
        map.hideMap = hideMap
        map.debug = debug
        map.minSteps = 1
        map.maxSteps = 3
        map.parseMap(input)

        return map.minimalHeatLoss()
    }

    fun part2(input: List<String>, hideMap:Boolean = false, debug:Boolean = false): Int {
        print("\u001B[H\u001B[2J")
        val map = TownMap()
        map.hideMap = hideMap
        map.debug = debug
        map.minSteps = 4
        map.maxSteps = 10
        map.parseMap(input)

        return map.minimalHeatLoss()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
//    check(part1(testInput) == 102)

    val input = readInput("Day17")

    println("Day17:")
    measureRun("Part1") {
        val debug = false
        var res = part1(testInput, false, debug)
//        var res = part1(input, true, debug)
        println("Heatloss: $res")
    }
//    measureRun("Part2") {
//        val debug = true
//        var res = part2(testInput, false, debug)
////        var res = part2(input, true, debug)
//        println("Heatloss: $res")
//    }
}
