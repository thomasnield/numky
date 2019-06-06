package org.nield.numky.linear

import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.RealMatrixContext.elementContext
import scientifik.kmath.operations.sum
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.asSequence
import kotlin.math.pow


fun realMatrix(rowNum: Int, colNum: Int, initializer: (i: Int, j: Int) -> Double) = MatrixContext.real.produce(rowNum, colNum, initializer)

fun Sequence<DoubleArray>.toMatrix() = toList().let {
    MatrixContext.real.produce(it.size,it[0].size) { row, col -> it[row][col] }
}


operator fun Matrix<Double>.times(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@times[row, col] * double
}

fun Matrix<Double>.square() =  MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@square[row,col].let { it.pow(2) }
}

operator fun Matrix<Double>.plus(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@plus[row,col] + double
}

operator fun Matrix<Double>.minus(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@minus[row,col] - double
}

operator fun Matrix<Double>.div(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@div[row,col] / double
}

operator fun Double.times(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] * this
}

operator fun Double.plus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] + this
}

operator fun Double.minus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] - this
}

operator fun Double.div(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] / this
}

operator fun Matrix<Double>.times(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@times[row,col] * other[row,col]
}

operator fun Matrix<Double>.plus(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@plus[row,col] + other[row,col]
}

operator fun Matrix<Double>.minus(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@minus[row,col] - other[row,col]
}

fun Matrix<Double>.extractColumn(columnIndex: Int) = extractColumns(columnIndex..columnIndex)

fun Matrix<Double>.extractColumns(columnRange: IntRange) = MatrixContext.real.produce(rowNum, columnRange.count()) { row, col ->
    this@extractColumns[row, columnRange.start + col]
}


fun Matrix<Double>.sumByColumn() = MatrixContext.real.produce(1, colNum) { i, j ->
    val column = columns[j]
    with(elementContext) {
        sum(column.asSequence())
    }
}

fun Matrix<Double>.sum() = this.elements().map { (dim,value) -> value  }.sum()
fun Matrix<Double>.min() = this.elements().map { (dim,value) -> value  }.min()
fun Matrix<Double>.max() = this.elements().map { (dim,value) -> value  }.max()
fun Matrix<Double>.average() = this.elements().map { (dim,value) -> value  }.average()
