package model

enum class FieldEvent{
    OPEN,
    CHECK,
    UNCHECK,
    EXPLODE,
    RESTART
}

data class Field(val row: Int, val column: Int) {

    private val neighbours = ArrayList<Field>()
    private val callbacks = ArrayList<(Field, FieldEvent) -> Unit>()

    var checked: Boolean = false
    var opened: Boolean = false
    var mined: Boolean = false

    val goalAchieved: Boolean get() = !mined && opened || mined && checked
    val minedNeighbours: Int get() = neighbours.filter { it.mined }.size
    val safeNeighbourhood: Boolean
        get() = neighbours.map { !it.mined }.reduce { result, current -> result && current }

    fun addNeighbour(neighbour: Field) {
        neighbours.add(neighbour)
    }

    fun onEvent(callback: (Field, FieldEvent) -> Unit) {
        callbacks.add(callback)
    }

    fun open() {
        if(!opened) {
            opened = true
            if(mined) {
                callbacks.forEach { it(this, FieldEvent.EXPLODE) }
            } else {
                callbacks.forEach { it(this, FieldEvent.OPEN) }
                neighbours.filter { safeNeighbourhood && !it.opened && !it.mined }.forEach { it.open() }
            }
        }
    }

    fun updateCheck() {
        if(!opened) {
            checked = !checked
            val event = if(checked) FieldEvent.CHECK else FieldEvent.UNCHECK
            callbacks.forEach { it(this, event) }
        }
    }

    fun setMine() {
        mined = true
    }

    fun restart() {
        opened = false
        mined = true
        checked = true
        callbacks.forEach { it(this, FieldEvent.RESTART) }
    }
}