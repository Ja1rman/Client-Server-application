package datamanager

import entities.Difficulty
import entities.LabWork

/**
 * Object to get id and get operations
 */
object DataManager {
    private val data = mutableListOf<LabWork>()

    fun load(dat: MutableList<LabWork>) {
        data.clear()
        data.addAll(dat)
    }

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

    fun maxOfData() = data.maxOrNull()

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
