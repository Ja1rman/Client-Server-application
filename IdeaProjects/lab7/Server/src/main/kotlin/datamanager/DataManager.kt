package datamanager

import entities.Difficulty
import entities.LabWork

/**
 * Object to get id and get operations
 */
object DataManager {
    private val data = mutableListOf<LabWork>()

    /**
     * add new element
     * @param new for add new
     */
    fun add(new: LabWork) = if (data.find { it.id == new.id } != null) throw Exception("Id ${new.id} уже существует") else data.add(new)
    /**
     * @return Items Number
     */
    fun getItemsNumber() = data.size

    /**
     * @return all data
     */
    fun getAllItems() = data

    /**
     * upadate data by id
     * @param id id element
     * @param element element to update
     */
    fun updateById(id: Long, element: LabWork) {
        data[data.indexOfFirst { it.id == id }] = element.apply { this.id = id }
    }
    /**
     * remove data by id
     * @param id id element
     */
    fun removeById(id: Long) = data.removeIf { it.id == id }

    /**
     * clear all data
     */
    fun clearData() = data.clear()
    /**
     * remove last element
     */
    fun removeLast() = data.removeLast()
    /**
     * add element if it max
     * @param element element to add if it max
     */
    fun addIfMax(element: LabWork): Boolean {
        val max = data.maxOrNull()
        if (max == null || element > max) {
            add(element)
            return true
        }
        return false
    }
    /**
     * remove elements if it lower
     * @param element element to remove all if lower
     */
    fun removeLower(element: LabWork) = data.removeIf { it < element }
    /**
     * remove elements if it equal minimal point
     * @param minimalPoint comp with it
     */
    fun removeByMP(minimalPoint: Long) = data.removeIf { it.minimalPoint == minimalPoint }

    /**
     * @return sum all minimal point
     */
    fun sumMP() = data.sumOf { it.minimalPoint ?: 0 }

    /**
     * @param difficulty difficulty to comp
     * @return quant of elements less difficulty
     */
    fun countLessDifficulty(difficulty: Difficulty) = data.filter{ (it.difficulty?.index ?: 0) < difficulty.index }.size
}
