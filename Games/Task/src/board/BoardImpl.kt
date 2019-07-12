package board

import java.util.stream.Collectors

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width, createSquareBoard(width))
class SquareBoardImpl(final override val width: Int) : SquareBoard {

    val cells: List<Cell>

    init {
        cells = (1..width).flatMap { column -> (1..width).map { Cell(column, it) } }
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? =
            if (coordOutOfBounds(i, j)) null else cells.first { it.i == i && it.j == j }


    private fun coordOutOfBounds(i: Int, j: Int) = i > width || j > width || i <= 0 || j <= 0


    override fun getCell(i: Int, j: Int): Cell = getCellOrNull(i, j)!!

    override fun getAllCells(): Collection<Cell> = cells

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val r = cells.filter { it.i==i }.filter { it.j in jRange }
        return if (jRange.first > jRange.last) r.asReversed() else r
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val c = cells.filter { it.j==j }.filter { it.i in iRange }
        return if (iRange.first > iRange.last) c.asReversed() else c

    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction){
            Direction.DOWN -> cells.find{it.i==i+1 && it.j == j}
            Direction.UP -> cells.find{it.i==i-1 && it.j == j}
            Direction.RIGHT -> cells.find{it.i==i && it.j == j+1}
            Direction.LEFT -> cells.find{it.i==i && it.j == j-1}

        }
    }

}

class GameBoardImpl<T>(final override val width:Int, val squareBoard: SquareBoard):GameBoard<T>,SquareBoard by squareBoard{

    private var cellsWithValues:MutableMap<Cell,T?> = hashMapOf()

    override fun get(cell: Cell): T? = cellsWithValues[cell]

    override fun set(cell: Cell, value: T?) {
        cellsWithValues[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = getAllCells().filter { predicate(cellsWithValues[it]) }

    override fun find(predicate: (T?) -> Boolean): Cell? = getAllCells().find { predicate(get(it)) }

    override fun any(predicate: (T?) -> Boolean): Boolean = find(predicate) != null

    override fun all(predicate: (T?) -> Boolean): Boolean = getAllCells().all { predicate(get(it)) }


}