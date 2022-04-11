package datamanager

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import entities.RequestData
import io.Database

object SecureManager {
    private val mapper = jacksonObjectMapper()

    fun process(json: String): String {
        val command = mapper.readValue(json, RequestData::class.java)

        return if (command.command == "/reg")
            Database.addUser(command.userName, command.password)
        else if (command.command == "/l") {
            val userId = Database.checkUser(command.userName, command.password)

            if (userId != -1L) "ok" else "Ошибка аутентификации"
        } else {
            val userId = Database.checkUser(command.userName, command.password)

            if (userId != -1L) CommandResolver.resolve(command, userId) else "Ошибка аутентификации"
        }
    }
}