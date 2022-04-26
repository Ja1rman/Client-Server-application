package datamanager
import entities.*
import io.Database
import io.Logger.logger

object CommandResolver {
    fun resolve(command: RequestData, userId: Long): String {
        when (command.command) {
            "add" -> {
                try {
                    if (command.obj == null) {
                        logger.error("Null object received")
                        return "Вы дебил"
                    }

                    val response = add(command.obj, userId)
                    if (response != "ok") {
                        logger.info("Can't add elem")
                        return "Error"
                    }

                    logger.info("User: added new elem")
                    return "Added"
                } catch (e: Exception) {
                    logger.error("Error added elem, ${e.message}")
                    return "Can not add element\nError: ${e.message}"
                }
            }
            "show" -> {
                return try {
                    var data = ""
                    for (lab in DataManager.getAllItems()) {
                        data += "$lab id=${lab.id}\n"
                    }
                    logger.info("User: elements showed")
                    data
                } catch (e: Exception) {
                    logger.error("Error show elem, ${e.message}")
                    "Can not show elements\nError: ${e.message}"
                }
            }
            "remove_last" -> {
                return try {
                    val response = Database.removeLast(userId)
                    if (response == -1L){
                        logger.info("Can't remove last")
                        return "Error"
                    }
                    val elem = DataManager.removeById(response)
                    logger.info("User: last element removed")
                    "Removed element: $elem"
                } catch (e: Exception) {
                    logger.error("Error: remove last elem, ${e.message}")
                    "Can not remove element\nError: ${e.message}"
                }
            }
            "info" -> {
                return try {
                    val response = DataManager.getItemsNumber()
                    logger.info("User: info was given")
                    "Number of elements: $response"
                } catch (e: Exception) {
                    logger.error("Error: cant give info, ${e.message}")
                    "Can not get number of elements\nError: ${e.message}"
                }
            }
            "clear" -> {
                return try {
                    val response = Database.clear(userId)
                    if (response != "ok") {
                        logger.info("Can't remove lower")
                        return "Error"
                    }
                    DataManager.load(Database.loadAll())
                    logger.info("User: all info deleted")
                    "OK"
                } catch (e: Exception) {
                    logger.error("Error: cant clear info, ${e.message}")
                    "Can not clear elements\nError: ${e.message}"
                }
            }
            "remove_by_id" -> {
                try {
                    if (command.id == null) {
                        logger.error("Null id received")
                        return "Вы дебил"
                    }
                    val response = Database.removeId(command.id, userId)
                    if (response != "ok") {
                        logger.info(response)
                        return "Error"
                    }
                    val elem = DataManager.removeById(command.id)
                    logger.info("User: elem by id removed")
                    return elem.toString()
                } catch (e: Exception) {
                    logger.error("Error: cant remove elem by id, ${e.message}")
                    return "Can not remove element\nError: ${e.message}"
                }
            }
            "update" -> {
                try {
                    if (command.obj == null) {
                        logger.error("Null object received")
                        return "Вы дебил"
                    }
                    if (command.id == null) {
                        logger.error("Null id received")
                        return "Вы дебил"
                    }

                    val response = Database.update(command.obj, command.id, userId)
                    if (response != "ok") {
                        logger.info(response)
                        return "Error"
                    }

                    DataManager.updateById(
                        id = command.id,
                        command.obj
                    )
                    logger.info("User: elem by id updated")
                    return "Updated"
                } catch (e: Exception) {
                    logger.error("Error: cant update elem by id, ${e.message}")
                    return "Can not update element\nError: ${e.message}"
                }
            }
            "add_if_max" -> {
                try {
                    if (command.obj == null) {
                        logger.error("Null object received")
                        return "Вы дебил"
                    }
                    val response = DataManager.maxOfData()
                    if (!(response == null || command.obj > response)){
                        logger.info("Not Max Element")
                        return "Not Max"
                    }
                    val resp = add(command.obj, userId)
                    if (resp != "ok") {
                        logger.info("Can't add elem")
                        return "Error"
                    }
                    logger.info("User: elem add if max success")
                    return "Added"
                } catch (e: Exception) {
                    logger.error("Error: cant add max elem, ${e.message}")
                    return "Can not add element\nError: ${e.message}"
                }
            }
            "remove_lower" -> {
                try {
                    if (command.obj == null) {
                        logger.error("Null object received")
                        return "Вы дебил"
                    }

                    if (command.obj.minimalPoint == null) {
                        return "No elements for remove"
                    }

                    val response = Database.removeLower(command.obj.minimalPoint, userId)
                    if (response != "ok") {
                        logger.info("Can't remove lower")
                        return "Error"
                    }
                    DataManager.load(Database.loadAll())
                    logger.info("User: elem remove lower success")
                    return "OK"
                } catch (e: Exception) {
                    logger.error("Error: cant remove lower elem, ${e.message}")
                    return "Can not remove lower element\nError: ${e.message}"
                }
            }
            "remove_all_by_minimal_point" -> {
                try {
                    if (command.mp == null) {
                        logger.error("Null minimal point received")
                        return "Вы дебил"
                    }
                    val response = Database.removeByMP(command.mp, userId)
                    if (response != "ok") {
                        logger.info("Can't remove by mp")
                        return "Error"
                    }
                    DataManager.load(Database.loadAll())
                    logger.info("User: remove_all_by_minimal_point success")
                    return "OK"
                } catch (e: Exception) {
                    logger.error("Error: cant remove_all_by_minimal_point, ${e.message}")
                    return "Can not remove elements\nError: ${e.message}"
                }
            }
            "sum_of_minimal_point" -> {
                return try {
                    val response = DataManager.sumMP()
                    logger.info("User: sum_of_minimal_point success")
                    "Result: $response"
                } catch (e: Exception) {
                    logger.error("Error: cant sum_of_minimal_point, ${e.message}")
                    "Can not find sum of elements\nError: ${e.message}"
                }
            }
            "count_less_than_difficulty" -> {
                try {
                    if (command.difficulty == null) {
                        logger.error("Null difficulty received")
                        return "Вы дебил"
                    }
                    val response = DataManager.countLessDifficulty(command.difficulty)
                    logger.info("User: count_less_than_difficulty success")
                    return "Result: $response"
                } catch (e: Exception) {
                    logger.error("Error: cant count_less_than_difficulty, ${e.message}")
                    return "Can not count elements\nError: ${e.message}"
                }
            }
            "help" -> {
                logger.info("User: got help")
                return """
                    help : вывести справку по доступным командам
                    info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
                    show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
                    add {element} : добавить новый элемент в коллекцию
                    update id {element} : обновить значение элемента коллекции, id которого равен заданному
                    remove_by_id id : удалить элемент из коллекции по его id
                    clear : очистить коллекцию
                    execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
                    exit : завершить программу (без сохранения в файл)
                    remove_last : удалить последний элемент из коллекции
                    add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
                    remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный
                    remove_all_by_minimal_point minimalPoint : удалить из коллекции все элементы, значение поля minimalPoint которого эквивалентно заданному
                    sum_of_minimal_point : вывести сумму значений поля minimalPoint для всех элементов коллекции
                    count_less_than_difficulty difficulty : вывести количество элементов, значение поля difficulty которых меньше заданного
                """
            }
            else -> {
                logger.warn("User: Invalid command")
                return "Неизвестная команда"
            }
        }
    }
    private fun add(labWork: LabWork, userId: Long): String {
        val newId = Database.addLabWork(labWork, userId)

        if (newId == -1L) {
            logger.error("User: can't add new elem")
            return "Error"
        }

        DataManager.add(labWork.apply { id = newId })
        return "ok"
    }
}
