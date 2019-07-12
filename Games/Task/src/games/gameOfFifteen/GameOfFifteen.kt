package games.gameOfFifteen

import board.Cell
import board.Direction
import board.GameBoard
import board.createGameBoard
import games.game.Game

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game =
  GameOfFifteen(initializer)


class GameOfFifteen(private val initializer: GameOfFifteenInitializer) : Game {
    val board = createGameBoard<Int?>(4)

    val winningOrder = (1..15).toList().plus(null as Int?)

    private fun <T> sortedListOfCells(board: GameBoard<T>): List<Cell> {
        return board.getAllCells().sortedWith(compareBy<Cell> { it.i }.thenBy { it.j })
    }

    override fun initialize() {
        sortedListOfCells(board).forEachIndexed { index, cell ->
            if (index < initializer.initialPermutation.size)
                board[cell] = initializer.initialPermutation[index] else board[cell] = null
        }
    }

    override fun canMove(): Boolean {
        return true
    }

    override fun hasWon(): Boolean {
        return winningOrder == sortedListOfCells(board).map { board[it] }
    }

    override fun processMove(direction: Direction) {
        val emptyCell = board.find { it == null }
        val translatedDirection = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.RIGHT -> Direction.LEFT
            Direction.LEFT -> Direction.RIGHT
        }
        val neighbourCell = with(board) {
            emptyCell?.getNeighbour(translatedDirection)
        }
        makeMove(neighbourCell, emptyCell)
    }

    private fun makeMove(neighbourCell: Cell?, emptyCell: Cell?) {
        if (neighbourCell != null && emptyCell != null) {
            board[emptyCell] = board[neighbourCell]
            board[neighbourCell] = null
        }
    }

    override fun get(i: Int, j: Int): Int? {
        return board.run { get(getCell(i, j)) }
    }
}

