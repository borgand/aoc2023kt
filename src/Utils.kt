import java.math.BigInteger
import java.security.MessageDigest
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
