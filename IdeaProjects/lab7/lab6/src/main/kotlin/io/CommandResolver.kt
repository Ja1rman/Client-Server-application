package io

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import entities.Coordinates
import entities.Difficulty
import entities.Discipline
import entities.LabWork
import io.ServerConnection.client
import io.ServerConnection.output
import io.ServerConnection.input
import io.ServerConnection.login
import io.ServerConnection.password
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.SocketException

object CommandResolver {
    val mapper = jacksonObjectMapper()
    var scan = BufferedReader(InputStreamReader(System.`in`))
    fun resolve(command: List <String>){
        try {
            when (command[0]) {
                "exit" -> {
                    client.close()
                    return
                }
                "add" -> {
                    val res = enterVals()
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "add", "obj" to res))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "show" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "show"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "remove_last" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "remove_last"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "info" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "info"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "clear" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "clear"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "remove_by_id" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "remove_by_id", "id" to command[1]))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "update" -> {
                    val res = enterVals()
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "update",
                        "id" to command[1],
                        "obj" to res))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "add_if_max" -> {
                    val res = enterVals()
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "add_if_max",
                        "obj" to res))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "remove_lower" -> {
                    val res = enterVals()
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "remove_lower",
                        "obj" to res))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "remove_all_by_minimal_point" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "remove_all_by_minimal_point",
                        "mp" to command[1]))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "sum_of_minimal_point" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "sum_of_minimal_point"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "count_less_than_difficulty" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "count_less_than_difficulty",
                        "difficulty" to Difficulty.valueOf(command[1])))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "help" -> {
                    val json = mapper.writeValueAsString(mapOf("userName" to login, "password" to password, "command" to "help"))
                    output.println(json)
                    do println(input.readLine())
                    while (input.ready())
                }
                "execute_script" -> {
                    scan = BufferedReader(InputStreamReader(FileInputStream(command[1])))
                }
                else -> {
                    println("Вы ввели говно.")
                }
            }
        } catch (e: Exception) {
            if (e is SocketException){
                println("Сервер АФК")
                return
            }
            println("Error: ${e.message}")
        }
    }
    fun enterVals(): LabWork {
        var name: String
        while (true) {
            print("Enter name: ")
            try {
                name = scan.readLine()
                if (name == "" || name == "null") {
                    println("Name can not be empty")
                }
                else {
                    break
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        var entries: List<String>
        var coordinates: Coordinates
        while (true) {
            print("Enter coordinates: ")
            entries = scan.readLine().split(" ")
            try {
                coordinates = Coordinates(entries[0].toLong(), entries[1].toLong())
                break
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        var minimalPoint: Long?
        while (true) {
            print("Enter minimalPoint: ")
            try {
                val input = scan.readLine()
                if (input == ""){
                    minimalPoint = null
                    break
                }
                else if (input.toLong() <= 0) {
                    println("minimalPoint can not less than 0")
                }
                else {
                    minimalPoint = input.toLong()
                    break
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        var difficulty: Difficulty?
        while (true) {
            print("Enter difficulty (VERY_EASY, NORMAL, HARD, VERY_HARD, TERRIBLE): ")
            try {
                val input = scan.readLine()
                difficulty = if (input == "") null
                else Difficulty.valueOf(input)
                break
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        var discipline: Discipline
        while (true) {
            print("Enter discipline: ")
            entries = scan.readLine().split(" ")
            try {
                discipline = Discipline(if (entries[0] == "null") " " else entries[0], entries[1].toInt())
                break
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        return LabWork(
            name = name,
            coordinates = coordinates,
            minimalPoint = minimalPoint,
            difficulty = difficulty,
            discipline = discipline
        )
    }
}