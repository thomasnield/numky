package org.nield.numky

import org.nield.numky.linear.plus
import org.nield.numky.linear.realMatrix
import org.ojalgo.random.Normal
import scientifik.kmath.linear.dot
import scientifik.kmath.structures.Matrix
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp


fun main() {


}
class NeuralNetwork(val inputNodes: Int,
                    val middleNodes: Int,
                    val outerNodes: Int) {

    var middleLayerWeights = realMatrix(1, middleNodes) { row,col -> randomDouble(-1.0..1.0) }
    var middleLayerBiases = realMatrix(1, middleNodes)  { row,col -> randomDouble(-1.0..1.0) }

    var outerLayerWeights = realMatrix(1, outerNodes) { row,col -> randomDouble(-1.0..1.0) }
    var outerLayerBiases = realMatrix(1, outerNodes)  { row,col -> randomDouble(-1.0..1.0) }

    fun evaluate(inputColors: Matrix<Double>) {
        val output = ((inputColors dot middleLayerWeights + middleLayerBiases) dot outerLayerWeights) + outerLayerBiases
    }

    fun train(inputColors: Matrix<Double>, expectedResult: Matrix<Double>) {

    }
}

fun Matrix<Double>.templateRandomAdjust(): Matrix<Double> {
    val randomRow = randomInt(0..rowNum)
    val randomCol = randomInt(0..colNum)

    return realMatrix(rowNum, colNum) { row,col ->
        if (row == randomRow && col == randomCol)
            randomNormal()
        else
            0.0
    }
}

fun Matrix<Double>.tanh() = realMatrix(rowNum, colNum) { row,col ->  kotlin.math.tanh(this[row,col])}
fun Matrix<Double>.sigmoid() = realMatrix(rowNum, colNum) { row,col ->  1.0 / (1.0 + exp(- 1.0 / 1.0 + exp(-this[row,col]))) }
fun Matrix<Double>.relu() = realMatrix(rowNum, colNum) { row,col ->  if (this[row,col] < 0.0) 0.0 else this[row,col] }
/*fun Matrix<Double>.softmax() = realMatrix(rowNum, colNum) { row,col ->
    val sumValues = rows[]
    (exp(x) / otherValues().asSequence().map { exp(it) }.sum())
}*/

val normal = Normal(0.0,1.0)
fun randomNormal() = normal.get()
fun randomInt(intRange: IntRange) = ThreadLocalRandom.current().nextInt(intRange.start, intRange.endInclusive)
fun randomDouble(doubleRange: ClosedRange<Double>) = ThreadLocalRandom.current().nextDouble(doubleRange.start, doubleRange.endInclusive)