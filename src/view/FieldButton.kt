package view

import model.Field
import model.FieldEvent
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.SwingUtilities

private val NORMAL_BG_COLOR = Color(184, 184, 184)
private val CHECKED_BG_COLOR = Color(8, 179, 247)
private val EXPLODED_BG_COLOR = Color(189, 66, 68)
private val TEXT_COLOR_GREEN = Color(0, 100, 0)

class FieldButton(private val field: Field): JButton() {
    init {
        font = font.deriveFont(Font.BOLD)
        background = NORMAL_BG_COLOR
        isOpaque = true
        border = BorderFactory.createBevelBorder(0)
        addMouseListener(MouseClickListener(field, { it.open() }, { it.updateCheck() }))

        field.onEvent(this::applyStyle)
    }

    private fun applyStyle(field: Field, event: FieldEvent) {
        when(event) {
            FieldEvent.EXPLODE -> applyStyleExploded()
            FieldEvent.OPEN -> applyStyleOpened()
            FieldEvent.CHECK -> applyStyleChecked()
            else -> applyStyleNormal()
        }

        SwingUtilities.invokeLater {
            repaint()
            validate()
        }
    }

    private fun applyStyleExploded() {
        background = EXPLODED_BG_COLOR
        text = "X"
    }

    private fun applyStyleOpened() {
        background = NORMAL_BG_COLOR
        border = BorderFactory.createLineBorder(Color.GRAY)

        foreground = when(field.minedNeighbours) {
            1 -> TEXT_COLOR_GREEN
            2 -> Color.BLUE
            3 -> Color.YELLOW
            4, 5, 6 -> Color.RED
            else -> Color.PINK
        }

        text = if(field.minedNeighbours > 0) field.minedNeighbours.toString() else ""
    }

    private fun applyStyleChecked() {
        background = CHECKED_BG_COLOR
        foreground = Color.BLACK
        text = "M"
    }

    private fun applyStyleNormal() {
        background = NORMAL_BG_COLOR
        border = BorderFactory.createBevelBorder(0)
        text = ""
    }
}