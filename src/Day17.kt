import java.util.PriorityQueue

// Keep primary constructor without heatloss for equality checks
data class State(val pos:Point, val dir:Direction = Direction.R){
    var heatloss:Int = Int.MAX_VALUE
    var path:List<Point> = listOf(pos)

    // override the primary constructor to allow for setting heatloss
    constructor(pos:Point, heatloss:Int, dir:Direction):this(pos, dir){
        this.heatloss = heatloss
    }
}


class TownMap{
    val map = mutableMapOf<Point,Int>()
    // the rules on the steps the lava crucible can take
    var minSteps:Int = 1
    var maxSteps:Int = 3


    fun get(pos:Point):Int{
        return map.getOrDefault(pos,0)
    }
    fun set(pos:Point, value:Int){
        map[pos] = value
    }

    fun width():Int{
        return (map.keys.maxByOrNull { it.x }?.x ?: 0) + 1
    }

    fun height():Int{
        return (map.keys.maxByOrNull { it.y }?.y ?: 0) + 1
    }

    fun validPoint(pos:Point):Boolean{
        return pos.x >= 0 && pos.x < width() && pos.y >= 0 && pos.y < height()
    }



    fun parseMap(input:List<String>){
        // set coordinate system so that Y increases downwards
        Direction.coordinateSystem = CoordinateSystem.SCREEN
        input.forEachIndexed{y,line ->
            val digits = line.map { c -> c.digitToInt()}
            digits.forEachIndexed{x,d ->
                set(Point(x,y),d)
            }
        }
    }
    fun getHeatLoss(pos:Point):Int{
        return get(pos)
    }

    fun getConnections(state:State):List<State>{
        val out = mutableListOf<State>()
        var curr = state
        var next = state
        val dirs = mutableListOf<Direction>()
        // add turns, while avoiding turning back
        if (state.dir != Direction.U) {
            dirs.add(state.dir.turnLeft())
        }
        if (state.dir != Direction.L) {
            dirs.add(state.dir.turnRight())
        }
        for (i in 1 ..maxSteps ) {
            // make min steps without turning
            if (i >= minSteps - 1) {
                for (d in dirs) {
                    val p = d.move(next.pos)
                    if (!validPoint(p)) {
                        continue
                    }
                    val s = State(pos = p, dir = d, heatloss = next.heatloss + getHeatLoss(p))
                    s.path = next.path + s.pos
                    out.add(s)
                }
            }

            // we've discovered all neighbours, no need to continue
            if (i >= maxSteps) {
                break
            }

            val pos = curr.dir.move(curr.pos)
            next = State(pos = pos, dir = curr.dir, heatloss = curr.heatloss + getHeatLoss(pos))
            next.path = curr.path + next.pos
            if (!validPoint(next.pos)) {
                break
            }
//            out.add(next)
            curr = next
        }
        return out
    }

    fun solve(start:Point, end:Point):Int {
        val visited = mutableSetOf<State>()
        val comparator = compareBy<State> { it.heatloss}
        val queue = PriorityQueue(comparator)
        val heatmap = mutableMapOf<Point,Int>()
        var endState = State(end)
        // generate initial right
        var pos = Direction.R.move(start)
        var state = State(pos, getHeatLoss(pos), Direction.R)
        queue.add(state)
        // generate initial down
        pos = Direction.D.move(start)
        state = State(pos, getHeatLoss(pos), Direction.D)
        queue.add(state)
        var current:State
        var count = 0

        while(queue.isNotEmpty()) {
            current = queue.poll()
            if (!validPoint(current.pos)) {
                continue
            }
            if (current.pos == end){
                endState = current
                break
            }

            val conns = getConnections(current)
            for (s in conns) {
                if (s in visited) {
                    continue
                }

                if (heatmap.getOrDefault(s.pos, Int.MAX_VALUE) <= s.heatloss) {
                    continue
                }

                heatmap[s.pos] = s.heatloss
                queue.add(s)
            }
            if(count % 10 == 0){
//                printMap(current, visited, queue)
//                readLine()
            }
            count++
            visited.add(current)
        }

//        printMap(endState, visited, queue)
        println("Steps: $count")
//        debugPos(Point(11,5), heatmap, visited, queue)
//        debugPos(Point(11,6), heatmap, visited, queue)
        return endState.heatloss
    }

    fun debugPos(pos:Point, heatmap:Map<Point,State>, visited:MutableSet<State>, queue:PriorityQueue<State>){
        val s = heatmap.getOrDefault(pos, State(pos))
        println("Pos: $pos, Heatloss: ${s.heatloss}")
        printMap(s, visited, queue, false)
    }

    fun printMap(state:State, visited:MutableSet<State>, queue:PriorityQueue<State>, clearScreen:Boolean = true){
        val visitedPos = visited.map { it.pos }.toSet()
        val queuePos = queue.map { it.pos }.toSet()
        val pathPos = state.path.toSet()
        // move caret to top left
        if (clearScreen)
            print("\u001B[H")
        for (y in 0 until height()){
            for (x in 0 until width()){
                val pos = Point(x,y)
                val heatloss = getHeatLoss(pos)

                // set background colour to green if in visited set and yellow if in queue
                val colour = when {
                    pos == state.pos -> "\u001B[44m"
                    pos in pathPos -> "\u001B[41m"
                    pos in queuePos -> "\u001B[30;43m"
                    pos in visitedPos -> "\u001B[30;42m"
                    else -> ""
                }
                print("$colour$heatloss\u001B[0m")
            }
            println()
        }
    }

    fun minimalHeatLoss():Int{
        val start = Point(0,0)
        val end = Point(width() - 1, height() - 1)

        val minHeatloss = solve(start, end)
        return minHeatloss
    }
}
fun main() {
    fun part1(input: List<String>, debug:Boolean = false): Int {
        print("\u001B[H\u001B[2J")
        val map = TownMap()
        map.minSteps = 1
        map.maxSteps = 3
        map.parseMap(input)

        return map.minimalHeatLoss()
    }

    fun part2(input: List<String>, debug:Boolean = false): Int {
        print("\u001B[H\u001B[2J")
        val map = TownMap()
        map.minSteps = 4
        map.maxSteps = 10
        map.parseMap(input)

        return map.minimalHeatLoss()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
//    check(part1(testInput) == 102)

    val input = readInput("Day17")

    println("Day17:")
    measureRun("Part1") {
        val debug = false
//        var res = part1(testInput, debug)
        var res = part1(input, debug)
        println("Heatloss: $res")
    }
//    measureRun("Part2") {
//        val debug = true
//        var res = part2(testInput, debug)
////        var res = part2(input, debug)
//        println("Heatloss: $res")
//    }
}
