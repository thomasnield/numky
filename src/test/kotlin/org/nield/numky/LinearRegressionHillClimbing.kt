import org.nield.numky.linear.*
import org.ojalgo.random.Normal
import java.net.URL

fun main() {

    val points = URL("https://tinyurl.com/y58sesrr")
            .readText().split(Regex("\\r?\\n"))
            .asSequence()
            .drop(1)
            .filter { it.isNotEmpty() }
            .map { it.split(",").map { it.toDouble() }.toDoubleArray() }
            .toMatrix()

    val x = points.extractColumn(0)
    val y = points.extractColumn(1)

    val normal = Normal(0.0,1.0)

    val epochs = 200_000

    val n = x.rowNum.toDouble()

    var m = 0.0
    var b = 0.0

    var bestLoss = 10000000000000.0

    repeat(epochs){
        val mAdjust = normal.get()
        val bAdjust = normal.get()

        m += mAdjust
        b += bAdjust

        val newLoss = ((y - (x * m + b)).square() ).sum() / n

        if (newLoss < bestLoss) {
            bestLoss = newLoss
        } else {
            m -= mAdjust
            b -= bAdjust
        }
    }

    println("f(x) = ${m}x + $b")
}


