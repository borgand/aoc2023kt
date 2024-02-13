import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines

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

    fun turnRight():Direction{
        return when(this){
            U -> R
            D -> L
            L -> U
            R -> D
        }
    }
    fun move(pos: Pair<Int,Int>):Pair<Int,Int>{
        return Pair(pos.first + step.first, pos.second + step.second)
    }

    companion object {
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
