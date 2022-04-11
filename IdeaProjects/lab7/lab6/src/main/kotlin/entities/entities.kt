package entities

import java.util.*

/**
 * data class for storing elements
 * @param id automatic generating
 * @param name name of element
 * @param coordinates 2 coords of element
 * @param minimalPoint minimal point
 * @param difficulty one of 5 difficulty
 * @param creationDate date then this was created. automatic generating
 * @param discipline name and selfStudyHours
 */
data class LabWork(var id: Long = -1,
                   val name: String,
                   val coordinates: Coordinates,
                   val minimalPoint: Long? = null,
                   val difficulty: Difficulty? = null,
                   val creationDate: Date = Date(),
                   val discipline: Discipline) : Comparable<LabWork> {

    override fun compareTo(other: LabWork) = ((this.minimalPoint ?: 0) - (other.minimalPoint ?: 0)).toInt()
}

/**
 * class to hold cords
 * @param x x cord
 * @param y y cord
 */
class Coordinates(
    _x: Long,
    val y: Long) {
    val x = if (_x > -515) _x else throw Exception("Parameter 'x' can not be less than -515")

    override fun toString(): String {
        return "coordinates(x=${x}, y=${y})"
    }
}

/**
 * class to hold discipline info
 * @param name name of Discipline
 * @param selfStudyHours quant of study hours
 */
data class Discipline(val name: String,
                      val selfStudyHours: Int)

/**
 * enum class to discribe Difficulty
 * @param index equal difficulty to int
 */
enum class Difficulty(val index: Int) {
    VERY_EASY(0), NORMAL(1), HARD(2), VERY_HARD(3), TERRIBLE(4)
}