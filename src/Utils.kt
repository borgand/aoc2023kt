import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.math.abs

data class Point(val x: Int, val y: Int)

enum class CoordinateSystem{
    CARTESIAN, SCREEN
}
enum class Direction(private val step: Point){
    U(Point(0,1)),
    D(Point(0,-1)),
    L(Point(-1,0)),
    R(Point(1,0));

    fun opposite():Direction{
        return when(this){
            U -> D
            D -> U
            L -> R
            R -> L
        }
    }

    fun turnRight():Direction{
        return when(this){
            U -> R
            D -> L
            L -> U
            R -> D
        }
    }

    fun turnLeft():Direction{
        return when(this){
            U -> L
            D -> R
            L -> D
            R -> U
        }
    }

    fun move(pos: Pair<Int,Int>, len:Int = 1):Pair<Int,Int>{
        return Pair(pos.first + step.x * len, pos.second + step.y * len * yOrient())
    }

    fun move(pos: Point, len:Int = 1):Point{
        return Point(pos.x + step.x * len, pos.y + step.y * len * yOrient())
    }

    companion object {
        var coordinateSystem = CoordinateSystem.CARTESIAN

        fun yOrient():Int{
            return when(coordinateSystem){
                CoordinateSystem.CARTESIAN -> 1
                CoordinateSystem.SCREEN -> -1
            }
        }
        fun fromString(s:String):Direction{
            return when(s){
                "U" -> U
                "D" -> D
                "L" -> L
                "R" -> R
                else -> throw IllegalArgumentException("Invalid direction: $s")
            }
        }
    }
}

fun dirListToPath(dirList:List<Direction>, pos:Pair<Int,Int> = 0 to 0):List<Pair<Int,Int>>{
    return dirList.fold(listOf(pos)){acc, dir -> acc + dir.move(acc.last())}
}

fun dirAndLenToPoints(dirLens:List<Pair<Direction, Int>>, pos:Pair<Int,Int> = 0 to 0):List<Pair<Int,Int>>{
    val out = mutableListOf<Pair<Int,Int>>()
    out.add(pos)
    var next = pos
    for ((dir, len) in dirLens){
        next = dir.move(next, len)
        out.add(next)
    }
    return out
}


// Shoelace formula to calculate area of a polygon
// assumes points are ordered counter-clockwise
// NOTE: in grid coordinates with boundary, this omits the boundary points themselves
fun shoelaceArea(points:List<Pair<Int,Int>>):Long{
    var area = 0L
    var s1 = 0L
    var s2 = 0L
    for(i in 0 until points.size - 1){
        val p1 = points[i]
        val p2 = points[(i+1) % points.size]
//        s1 += p1.first.toLong() * p2.second.toLong()
//        s2 += p1.second.toLong() * p2.first.toLong()
        area += (p1.first.toLong() * p2.second.toLong()) - (p1.second.toLong() * p2.first.toLong())
    }
    // last point to first point
//    val p1 = points[points.size - 1]
//    val p2 = points[0]
//    area += (p1.first * p2.second).toLong() - (p1.second * p2.first).toLong()
    return abs(area) / 2L
}

// Pick's theorem for polygon (grid) area
// A = I + B/2 - 1
fun picksArea(points:List<Pair<Int,Int>>):Long{
    val i = shoelaceArea(points)
    val b = points.size
    return i + b/2 - 1
}

// function to detect first inner tile in a path of Pair<Int,Int> in given Direction by walking
// from the start position until we find an adjacent tile in Direction that is not wall itself
// (checking on the right hand side of the path)
fun firstInnerTile(path:List<Pair<Int,Int>>, dir:Direction):Pair<Int,Int>?{
    val checkDir = dir.turnRight()
    for(pos in path){
        val nextPath = dir.move(pos)
        val nextCheck = checkDir.move(nextPath)
        if(nextCheck !in path){
            return nextCheck
        }
    }
    return null
}

fun floodFill(pos:Pair<Int,Int>, isWall:(Pair<Int,Int>)->Boolean, visit:(Pair<Int,Int>)->Unit){
    // LinkedList is O(1) for add and remove
    val queue = LinkedList<Pair<Int,Int>>()
    queue.add(pos)
    val visited = mutableSetOf<Pair<Int,Int>>()

    while(queue.isNotEmpty()){
        val current = queue.removeFirst()
        visit(current)
        visited.add(current)
        for(dir in Direction.values()){
            val next = dir.move(current)
            if(next !in visited && !isWall(next) && !queue.contains(next)){
                queue.add(next)
            }
        }
    }
}

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

fun measureRun(name: String, block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("$name: ${end - start}ms")
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.printIt() = println(this)
