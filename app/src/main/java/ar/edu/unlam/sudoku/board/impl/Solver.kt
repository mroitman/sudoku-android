package ar.edu.unlam.sudoku.board.impl

import ar.edu.unlam.sudoku.board.config.Constants

internal object Solver {

    lateinit var grid: Array<IntArray>

    fun solvable(grid: Array<IntArray>) : Boolean {
        Solver.grid = grid.copy()

        return solve()
    }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    private fun solve() : Boolean {
        for (row in 0 until Constants.GRID_SIZE) {
            for (column in 0 until Constants.GRID_SIZE) {
                if (grid[row][column] == 0) {
                    val availableDigits = getAvailableDigits(row, column)
                    for (digit in availableDigits) {
                        grid[row][column] = digit
                        if (solve()) {
                            return true
                        }
                        grid[row][column] = 0
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun getAvailableDigits(row: Int, column: Int) : Iterable<Int> {
        val digitsRange = Constants.MIN_DIGIT_VALUE..Constants.MAX_DIGIT_VALUE
        var availableDigits = mutableSetOf<Int>()
        availableDigits.addAll(digitsRange)

        truncateByDigitsAlreadyUsedInRow(availableDigits, row)
        if (availableDigits.size > 1) {
            truncateByDigitsAlreadyUsedInColumn(availableDigits, column)
        }
        if (availableDigits.size > 1) {
            truncateByDigitsAlreadyUsedInBox(availableDigits, row, column)
        }

        return availableDigits.asIterable()
    }

    private fun truncateByDigitsAlreadyUsedInRow(availableDigits: MutableSet<Int>, row: Int) {
        for (column in Constants.MIN_DIGIT_INDEX..Constants.MAX_DIGIT_INDEX) {
            if (grid[row][column] != 0) {
                availableDigits.remove(grid[row][column])
            }
        }
    }

    private fun truncateByDigitsAlreadyUsedInColumn(availableDigits: MutableSet<Int>, column: Int) {
        for (row in Constants.MIN_DIGIT_INDEX..Constants.MAX_DIGIT_INDEX) {
            if (grid[row][column] != 0) {
                availableDigits.remove(grid[row][column])
            }
        }
    }

    private fun truncateByDigitsAlreadyUsedInBox(availableDigits: MutableSet<Int>, row: Int, column: Int) {
        val rowStart = findBoxStart(row)
        val rowEnd = findBoxEnd(rowStart)
        val columnStart = findBoxStart(column)
        val columnEnd = findBoxEnd(columnStart)

        for (i in rowStart until rowEnd) {
            for (j in columnStart until columnEnd) {
                if (grid[i][j] != 0) {
                    availableDigits.remove(grid[i][j])
                }
            }
        }
    }

    private fun findBoxStart(index: Int) = index - index % Constants.GRID_SIZE_SQUARE_ROOT

    private fun findBoxEnd(index: Int) = index + Constants.QUADRANT_SIZE - 1
}