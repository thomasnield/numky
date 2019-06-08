package org.nield.numky

import org.nield.numky.linear.*
import org.ojalgo.random.Normal
import scientifik.kmath.linear.RealMatrix
import scientifik.kmath.linear.dot
import scientifik.kmath.structures.Matrix
import java.net.URL
import java.time.Duration
import java.time.Instant
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
    val expectedOutputs = inputData.extractColumn(3).appendColumn { if (it[0] == 0.0) 1.0 else 0.0 }

    val nn = NeuralNetwork(3, 3, 2)

    nn.train(inputColors, expectedOutputs)
}


class NeuralNetwork(
    val inputNodes: Int,
    val middleNodes: Int,
    val outerNodes: Int
) {

    private class NeuralNetworkState(
        inputNodes: Int,
        middleNodes: Int,
        outerNodes: Int
    ) {
        var middleLayerWeights: RealMatrix = realMatrix(inputNodes, middleNodes) { _, _ -> randomDouble(-1.0..1.0) }
        var middleLayerBiases: RealMatrix = realMatrix(1, middleNodes) { _, _ -> randomDouble(-1.0..1.0) }
        var outerLayerWeights: RealMatrix = realMatrix(middleNodes, outerNodes) { _, _ -> randomDouble(-1.0..1.0) }
        var outerLayerBiases: RealMatrix = realMatrix(1, outerNodes) { _, _ -> randomDouble(-1.0..1.0) }
    }

    private fun NeuralNetworkState.evaluate(inputColors: Matrix<Double>): Matrix<Double> {

        val n = inputColors.rowNum

        val middleLayerOp = ((inputColors dot middleLayerWeights) + middleLayerBiases.repeatStackVertical(n)).tanh()
        val outerLayerOp = ((middleLayerOp dot outerLayerWeights) + outerLayerBiases.repeatStackVertical(n)).softmax()

        return outerLayerOp
    }

    fun train(inputColors: Matrix<Double>, actuals: Matrix<Double>) {

        var bestLoss = Double.MAX_VALUE

        NeuralNetworkState(inputNodes, middleNodes, outerNodes).run {

            val middleLayerWeightCount = middleLayerWeights.let { it.rowNum * it.colNum }
            val middleLayerBiasCount = middleLayerBiases.let { it.rowNum * it.colNum }
            val outerLayerWeightCount = outerLayerWeights.let { it.rowNum * it.colNum }
            val outerLayerBiasCount = outerLayerBiases.let { it.rowNum * it.colNum }

            val totalVariableCount =
                middleLayerWeightCount + middleLayerBiasCount + outerLayerWeightCount + outerLayerBiasCount

            val start = Instant.now()

            repeat(100_000) { step ->

                if (step != 0 && step % 100 == 0) {
                    val now = Instant.now()
                    println("$step cycles done in ${Duration.between(start, now)}")
                }

                val weightedRandom = (0 until totalVariableCount).random()

                val randomMatrixIndex = when {
                    weightedRandom < middleLayerWeightCount -> 0
                    weightedRandom < middleLayerWeightCount + middleLayerBiasCount -> 1
                    weightedRandom < middleLayerWeightCount + middleLayerBiasCount + outerLayerWeightCount -> 2
                    weightedRandom < middleLayerWeightCount + middleLayerBiasCount + outerLayerWeightCount + outerLayerBiasCount -> 3
                    else -> throw Exception("!")
                }
                val randomAdjust = when (randomMatrixIndex) {
                    0 -> middleLayerWeights.templateRandomAdjust(-1.0, 1.0)
                    1 -> middleLayerBiases.templateRandomAdjust(0.0, 1.0)
                    2 -> outerLayerWeights.templateRandomAdjust(-1.0, 1.0)
                    3 -> outerLayerBiases.templateRandomAdjust(0.0, 1.0)
                    else -> throw Exception("!")
                }

                when (randomMatrixIndex) {
                    0 -> middleLayerWeights += randomAdjust
                    1 -> middleLayerBiases += randomAdjust
                    2 -> outerLayerWeights += randomAdjust
                    3 -> outerLayerBiases += randomAdjust
                    else -> throw Exception("!")
                }

                val predictions = evaluate(inputColors)
                val loss = (predictions - actuals).pow(2).sum()

                if (loss < bestLoss) {
                    println("[$step] LOSS: $bestLoss -> $loss")
                    bestLoss = loss
                } else {
                    when (randomMatrixIndex) {
                        0 -> middleLayerWeights -= randomAdjust
                        1 -> middleLayerBiases -= randomAdjust
                        2 -> outerLayerWeights -= randomAdjust
                        3 -> outerLayerBiases -= randomAdjust
                        else -> throw Exception("!")
                    }
                }
            }
        }
    }
}

fun Matrix<Double>.templateRandomAdjust(min: Double, max: Double): Matrix<Double> {
    val randomRow = randomInt(0..rowNum)
    val randomCol = randomInt(0..colNum)

    return realMatrix(rowNum, colNum) { row, col ->
        if (row == randomRow && col == randomCol)
            (randomNormal() * .1).let {
                val currentValue = this@templateRandomAdjust[row, col]
                when {
                    currentValue + it < min -> min - currentValue
                    currentValue + it > max -> max - currentValue
                    else -> it
                }
            }
        else
            0.0
    }
}

fun Matrix<Double>.tanh() = realMatrix(rowNum, colNum) { row, col -> kotlin.math.tanh(this[row, col]) }
fun Matrix<Double>.sigmoid() =
    realMatrix(rowNum, colNum) { row, col -> 1.0 / (1.0 + exp(-1.0 / 1.0 + exp(-this[row, col]))) }

fun Matrix<Double>.relu() = realMatrix(rowNum, colNum) { row, col -> if (this[row, col] < 0.0) 0.0 else this[row, col] }

fun Matrix<Double>.softmax() = realMatrix(rowNum, colNum) { row, col ->
    var sumValues = 0.0
    for (i in 0 until rowNum) {
        sumValues += exp(get(i, col))
    }
    //val sumValues = columns[col].asSequence().map { exp(it) }.sum()
    exp(this[row, col]) / sumValues
}

val normal = Normal(0.0, 1.0)
fun randomNormal() = normal.get()
fun randomInt(intRange: IntRange) = ThreadLocalRandom.current().nextInt(intRange.start, intRange.endInclusive)
fun randomDouble(doubleRange: ClosedRange<Double>) =
    ThreadLocalRandom.current().nextDouble(doubleRange.start, doubleRange.endInclusive)