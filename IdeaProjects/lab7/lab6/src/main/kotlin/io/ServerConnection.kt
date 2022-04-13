package io

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ConnectException
import java.net.Socket

object ServerConnection {
    lateinit var client: Socket
    lateinit var output: PrintWriter
    lateinit var input: BufferedReader
    lateinit var login: String
    lateinit var password: String
    fun connect(){
        try {
            client = Socket("127.0.0.1", 8080)
            output = PrintWriter(client.getOutputStream(), true)
            input = BufferedReader(InputStreamReader(client.inputStream))
        } catch (e: Exception)
        {
            if (e is ConnectException) println("Сервек АФК")
            println("Error: ${e.message}")
            return
        }
    }
    fun reg(userName: String,
            passwrd: String): String {
        val json = CommandResolver.mapper.writeValueAsString(mapOf("command" to "/reg", "userName" to userName, "password" to passwrd))
        output.println(json)
        login = userName
        password = passwrd
        return input.readLine()
    }
    fun login(userName: String,
              passwrd: String): String  {
        val json = CommandResolver.mapper.writeValueAsString(mapOf("command" to "/l", "userName" to userName, "password" to passwrd))
        output.println(json)
        login = userName
        password = passwrd
        return input.readLine()
    }
}