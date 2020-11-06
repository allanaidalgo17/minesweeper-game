package model

import java.util.Random

enum class BoardEvent {
    VICTORY,
    DEFEAT
}

class Board(val totalRows: Int, val totalColumns: Int, val totalMines: Int) {
    private val fields = ArrayList<ArrayList<Field>>()
    private val callbacks = ArrayList<(BoardEvent) -> Unit>()

    init {
        generateFields()
        associateNeighbours()
        setMines()
    }

    private fun generateFields() {
        for(row in 0 until totalRows) {
            fields.add(ArrayList())
            for(column in 0 until totalColumns) {
                val newField = Field(row, column)
                newField.onEvent(this::verifyDefeat)
                fields[row].add(newField)
            }
        }
    }

    private fun associateNeighbours() {
        forEachField { associateNeighbours(it) }
    }

    private fun associateNeighbours(field: Field) {
        val (row, column) = field
        val rows = arrayOf(row - 1, row, row + 1)
        val columns = arrayOf(column - 1, column, column + 1)

        rows.forEach { r ->
            columns.forEach { c ->
                val current = fields.getOrNull(r)?.getOrNull(c)
                current.takeIf { field != it }?.let { field.addNeighbour(it) }
            }
        }
    }

    private fun setMines() {
        val generator = Random()

        var drawnRow = -1
        var drawnColumn = -1
        var currentMineNumber = 0

        while(currentMineNumber < this.totalMines) {
            drawnRow = generator.nextInt(totalRows)
            drawnColumn = generator.nextInt(totalColumns)

            val drawnField = fields[drawnRow][drawnColumn]
            if(!drawnField.mined) {
                drawnField.setMine()
                currentMineNumber++
            }
        }

    }

    private fun goalAchieved(): Boolean {
        var playerWon = true
        forEachField { if(!it.goalAchieved) playerWon = false }
        return playerWon
    }

    private fun verifyDefeat(field: Field, event: FieldEvent) {
        if(event == FieldEvent.EXPLODE) {
            callbacks.forEach { it(BoardEvent.DEFEAT) }
        } else if(goalAchieved()) {
            callbacks.forEach { it(BoardEvent.VICTORY) }
        }
    }

    fun forEachField(callback: (Field) -> Unit) {
        fields.forEach { row -> row.forEach(callback) }
    }

    fun onEvent(callback: (BoardEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun restart() {
        forEachField { it.restart() }
        setMines()
    }
}