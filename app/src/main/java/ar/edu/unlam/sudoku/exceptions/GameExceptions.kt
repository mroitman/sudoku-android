package ar.edu.unlam.sudoku.exceptions

abstract class GameException(message: String? = null, e: Throwable? = null) :
    Throwable(message, e)

class NumberOutOfRangeException(value: Int) :
    GameException("user entered $value and the value is out of range")

class FieldCannotBeSetException(row: Int, column: Int, action: String) :
    GameException("the row $row and $column cannot be set with action $action because the position is not an editable field")