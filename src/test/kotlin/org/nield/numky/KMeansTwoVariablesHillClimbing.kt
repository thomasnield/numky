import org.nield.numky.linear.realMatrix
import org.nield.numky.linear.toMatrix
import org.ojalgo.random.Normal
import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.zero
import scientifik.kmath.structures.Matrix
import java.net.URL
import java.util.concurrent.ThreadLocalRandom

// Desmos graph: https://www.desmos.com/calculator/pb4ewmqdvy

val points = URL("https://tinyurl.com/y25lvxug")
        .readText().split(Regex("\\r?\\n"))
        .asSequence()
        .drop(1)
        .filter { it.isNotEmpty() }
        .map { it.split(",").map { it.toDouble() }.toDoubleArray() }
        .toMatrix()

fun main() {

    val k = 4
    var centroids: Matrix<Double> = MatrixContext.real.zero(k,2)

    var bestLoss = Double.MAX_VALUE
/*
    repeat(100_000) {

        val prevCentroids = centroids

        val randomAdjust = centroids.templateRandomAdjust()
        centroids += randomAdjust

        val newLoss = points.asSequence()
                .map { pt ->
                    centroids.asSequence().map { distanceBetween(it, pt) }.min()!!.pow(2)
                }.sum()

        if (newLoss < bestLoss) {
            bestLoss = newLoss
        } else {
            centroids = prevCentroids
        }
    }

    centroids.forEach { println("${it.x},${it.y}") }*/
}

fun Matrix<Double>.templateRandomAdjust(): Matrix<Double> {
    val randomRow = randomInt(0..rowNum)
    val randomCol = randomInt(0..colNum)

    return realMatrix(rowNum, colNum) { row, col ->
        if (row == randomRow && col == randomCol) randomNormal() else 0.0
    }
}


val normal = Normal(0.0, 1.0)
fun randomNormal() = normal.get()
fun randomInt(intRange: IntRange) = ThreadLocalRandom.current().nextInt(intRange.start, intRange.endInclusive)
fun randomDouble(doubleRange: ClosedRange<Double>) =
        ThreadLocalRandom.current().nextDouble(doubleRange.start, doubleRange.endInclusive)