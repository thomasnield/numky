package org.nield.numky

import org.nield.numky.linear.*
import org.ojalgo.random.Normal
import scientifik.kmath.linear.dot
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.asSequence
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp


fun main() {

    val inputData = URL("https://tinyurl.com/y2qmhfsr")
            .readText().split(Regex("\\r?\\n"))
            .asSequence()
            .drop(1)
            .filter { it.isNotEmpty() }
            .map { it.split(",").map { it.toDouble() }.toDoubleArray() }
            .toMatrix()

    val n = inputData.rowNum

    val inputColors = inputData.extractColumns(0..2)
    val expectedOutputs = inputData.extractColumn(3)

    val nn = NeuralNetwork(3, 3, 2)


    nn.evaluate(inputColors).rows.asSequence().forEach {
        println(it.asSequence().joinToString(","))
    }
}
class NeuralNetwork(val inputNodes: Int,
                    val middleNodes: Int,
                    val outerNodes: Int) {

    var middleLayerWeights = realMatrix(inputNodes, middleNodes) { row,col -> randomDouble(-1.0..1.0) }
    var middleLayerBiases = realMatrix(1, middleNodes)  { row,col -> randomDouble(-1.0..1.0) }

    var outerLayerWeights = realMatrix(middleNodes, outerNodes) { row,col -> randomDouble(-1.0..1.0) }
    var outerLayerBiases = realMatrix(1, outerNodes)  { row,col -> randomDouble(-1.0..1.0) }

    fun evaluate(inputColors: Matrix<Double>): Matrix<Double> {

        val n = inputColors.rowNum

        val middleLayerOp = ((inputColors dot middleLayerWeights) + middleLayerBiases.repeatStackVertical(n)).tanh()
        val outerLayerOp = ((middleLayerOp dot outerLayerWeights) + outerLayerBiases.repeatStackVertical(n)).softmax()

        return outerLayerOp
    }

    fun train(inputColors: Matrix<Double>, actuals: Matrix<Double>) {

        var bestLoss = Double.MAX_VALUE

        repeat(100_000) {


            val predictions = evaluate(inputColors)

            val loss = (predictions - actuals).pow(2).sum()

            if (loss < bestLoss) {

            } else {

            }
        }

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

fun Matrix<Double>.softmax() = realMatrix(rowNum, colNum) { row,col ->
    val sumValues = columns[col].asSequence().map { exp(it) }.sum()
    exp(this[row,col]) / sumValues
}

val normal = Normal(0.0,1.0)
fun randomNormal() = normal.get()
fun randomInt(intRange: IntRange) = ThreadLocalRandom.current().nextInt(intRange.start, intRange.endInclusive)
fun randomDouble(doubleRange: ClosedRange<Double>) = ThreadLocalRandom.current().nextDouble(doubleRange.start, doubleRange.endInclusive)