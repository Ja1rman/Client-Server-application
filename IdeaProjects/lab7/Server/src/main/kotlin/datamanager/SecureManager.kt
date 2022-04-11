package datamanager

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import entities.RequestData
import io.Database

object SecureManager {
    private val mapper = jacksonObjectMapper()
    fun process(json: String): String{
        val command = mapper.readValue(json, RequestData::class.java)
        return if (command.command == "/reg")
            Database.addUser(command.userName, command.password)
        else if(command.command == "/l")
            Database.checkUser(command.userName, command.password)
        else {
            val response = Database.checkUser(command.userName, command.password)
            if (response == "ok") {
                CommandResolver.resolve(command)

            }
            else response
        }
    }
}