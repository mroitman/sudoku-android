package ar.edu.unlam.sudoku.board.impl

import androidx.annotation.StringRes
import ar.edu.unlam.sudoku.R
import ar.edu.unlam.sudoku.board.BoardService
import ar.edu.unlam.sudoku.board.config.Constants.Companion.GRID_SIZE
import ar.edu.unlam.sudoku.board.config.Constants.Companion.GRID_SIZE_SQUARE_ROOT
import ar.edu.unlam.sudoku.board.config.Constants.Companion.MAX_DIGIT_INDEX
import ar.edu.unlam.sudoku.board.config.Constants.Companion.MAX_DIGIT_VALUE
import ar.edu.unlam.sudoku.board.config.Constants.Companion.MIN_DIGIT_INDEX
import ar.edu.unlam.sudoku.board.config.Constants.Companion.MIN_DIGIT_VALUE
import ar.edu.unlam.sudoku.exceptions.FieldCannotBeSetException
import ar.edu.unlam.sudoku.exceptions.NumberOutOfRangeException

class BoardServiceImpl : BoardService {

    private val grid = Array(GRID_SIZE) { IntArray(GRID_SIZE) { 0 } }
    private val availablePositions = mutableListOf<EditableField>()
    private var solvedBoard = Array(GRID_SIZE) { IntArray(GRID_SIZE) { 0 } }

    override fun newGame(difficulty: Difficulty): Array<IntArray> {
        fillGrid(difficulty)
        return grid
    }

    override fun setValue(row: Int, column: Int, value: Int): Boolean {
        if (value !in MIN_DIGIT_VALUE..MAX_DIGIT_VALUE) throw NumberOutOfRangeException(value)

        if (notEditableField(row, column)) throw FieldCannotBeSetException(row, column, "SET")

        grid[row][column] = value

        return isCorrectNumber(row, column)
    }

    override fun deleteValue(row: Int, column: Int) {
        if (notEditableField(row, column)) throw FieldCannotBeSetException(row, column, "DELETE")
        grid[row][column] = 0
    }

    override fun isBoardSolved() = solvedBoard.contentEquals(grid)


    private fun fillGrid(difficulty: Difficulty) {
        fillDiagonalQuadrant()
        fillRemaining(0, GRID_SIZE_SQUARE_ROOT)
        saveSolvedGrid()
        removeDigits(difficulty)
    }

    private fun saveSolvedGrid() {
        for (row in 0 until GRID_SIZE) {
            for (column in 0 until GRID_SIZE) {
                solvedBoard[row][column] = grid[row][column]
            }
        }
    }

    private fun fillDiagonalQuadrant() {
        for (i in 0 until GRID_SIZE step GRID_SIZE_SQUARE_ROOT) {
            fillQuadrant(i, i)
        }
    }

    private fun fillQuadrant(row: Int, column: Int) {
        var generatedDigit: Int

        for (plusRowIndex in 0 until GRID_SIZE_SQUARE_ROOT) {
            for (plusColumnIndex in 0 until GRID_SIZE_SQUARE_ROOT) {
                do {
                    generatedDigit = generateRandomInt(MIN_DIGIT_VALUE, MAX_DIGIT_VALUE)
                } while (!isUnusedInBox(row, column, generatedDigit))

                grid[row + plusRowIndex][column + plusColumnIndex] = generatedDigit
            }
        }
    }

    private fun generateRandomInt(min: Int, max: Int) = (min..max).random()

    private fun isUnusedInBox(rowStart: Int, columnStart: Int, digit: Int): Boolean {
        for (plusRowIndex in 0 until GRID_SIZE_SQUARE_ROOT) {
            for (plusColumnIndex in 0 until GRID_SIZE_SQUARE_ROOT) {
                if (grid[rowStart + plusRowIndex][columnStart + plusColumnIndex] == digit) {
                    return false
                }
            }
        }
        return true
    }

    private fun fillRemaining(rowIndex: Int, columnIndex: Int): Boolean {
        var localRowIndex = rowIndex
        var localColumnIndex = columnIndex

        if (localColumnIndex >= GRID_SIZE && localRowIndex < GRID_SIZE - 1) {
            localRowIndex += 1
            localColumnIndex = 0
        }
        if (localRowIndex >= GRID_SIZE && localColumnIndex >= GRID_SIZE) {
            return true
        }
        if (localRowIndex < GRID_SIZE_SQUARE_ROOT) {
            if (localColumnIndex < GRID_SIZE_SQUARE_ROOT) {
                localColumnIndex = GRID_SIZE_SQUARE_ROOT
            }
        } else if (localRowIndex < GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
            if (localColumnIndex == (localRowIndex / GRID_SIZE_SQUARE_ROOT) * GRID_SIZE_SQUARE_ROOT) {
                localColumnIndex += GRID_SIZE_SQUARE_ROOT
            }
        } else {
            if (localColumnIndex == GRID_SIZE - GRID_SIZE_SQUARE_ROOT) {
                localRowIndex += 1
                localColumnIndex = 0
                if (localRowIndex >= GRID_SIZE) {
                    return true
                }
            }
        }

        for (digit in 1..MAX_DIGIT_VALUE) {
            if (isSafeToPutIn(localRowIndex, localColumnIndex, digit)) {
                grid[localRowIndex][localColumnIndex] = digit
                if (fillRemaining(localRowIndex, localColumnIndex + 1)) {
                    return true
                }
                grid[localRowIndex][localColumnIndex] = 0
            }
        }
        return false
    }

    private fun isUnusedInColumn(column: Int, digit: Int): Boolean {
        for (row in 0 until GRID_SIZE) {
            if (grid[row][column] == digit) {
                return false
            }
        }
        return true
    }

    private fun removeDigits(difficulty: Difficulty) {
        var digitsToRemove = GRID_SIZE * GRID_SIZE - difficulty.providedDigits

        while (digitsToRemove > 0) {
            val randomRow = generateRandomInt(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)
            val randomColumn = generateRandomInt(MIN_DIGIT_INDEX, MAX_DIGIT_INDEX)

            if (grid[randomRow][randomColumn] != 0) {
                val digitToRemove = grid[randomRow][randomColumn]
                grid[randomRow][randomColumn] = 0
                if (!Solver.solvable(grid)) {
                    grid[randomRow][randomColumn] = digitToRemove
                } else {
                    availablePositions.add(EditableField(randomRow, randomColumn))
                    digitsToRemove--
                }
            }
        }
    }

    private fun isSafeToPutIn(row: Int, column: Int, digit: Int) =
        isUnusedInBox(findBoxStart(row), findBoxStart(column), digit)
                && isUnusedInRow(row, digit)
                && isUnusedInColumn(column, digit)

    private fun findBoxStart(index: Int) = index - index % GRID_SIZE_SQUARE_ROOT

    private fun isCorrectNumber(row: Int, column: Int) =
        solvedBoard[row][column] == grid[row][column]

    private fun isEditableField(row: Int, column: Int) =
        availablePositions.contains(EditableField(row, column))

    private fun notEditableField(row: Int, column: Int) = !isEditableField(row, column)

    private fun isUnusedInRow(row: Int, digit: Int): Boolean {
        for (column in 0 until GRID_SIZE) {
            if (grid[row][column] == digit) {
                return false
            }
        }
        return true
    }

    data class EditableField(val rowIndex: Int, val columnIndex: Int)
    enum class Difficulty(@StringRes val displayText: Int, val providedDigits: Int) {
        HARD(R.string.hard, 10),
        MEDIUM(R.string.medium, 15),
        EASY(R.string.easy, 20),
        SUPER_EASY(R.string.super_easy, 25)
    }

}