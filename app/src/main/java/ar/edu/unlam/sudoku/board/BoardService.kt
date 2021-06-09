package ar.edu.unlam.sudoku.board

import ar.edu.unlam.sudoku.board.impl.BoardServiceImpl

interface BoardService {
    fun newGame(difficulty: BoardServiceImpl.Difficulty): Array<IntArray>
    fun setValue(row: Int, column: Int, value: Int): Boolean
    fun deleteValue(row: Int, column: Int)
    fun isBoardSolved(): Boolean
}