package view

import model.Board
import model.BoardEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

fun main() {
    MainScreen()
}

class MainScreen(): JFrame() {
    private val board = Board(totalRows = 16, totalColumns = 30, totalMines = 50)
    private val boardPanel = BoardPanel(board)

    init {
        board.onEvent(this::showResult)
        add(boardPanel)

        setSize(690, 438) // maybe will need tuning
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
        title = "Minesweep Game"
        isVisible = true
    }

    private fun showResult(event: BoardEvent) {
        SwingUtilities.invokeLater {
            val msg = when(event) {
                BoardEvent.VICTORY -> "You Won!"
                BoardEvent.DEFEAT -> "You Lost..."
            }

            JOptionPane.showMessageDialog(this, msg)
            board.restart()

            boardPanel.repaint()
            boardPanel.validate()
        }
    }
}