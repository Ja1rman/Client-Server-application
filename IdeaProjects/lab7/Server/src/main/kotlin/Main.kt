import java.io.BufferedReader
import java.io.InputStreamReader
import datamanager.SecureManager
import io.JsonConvertor
import java.io.PrintWriter
import java.net.ServerSocket
import io.Logger.logger
import kotlin.concurrent.thread
import sun.misc.Signal
import kotlin.system.exitProcess

fun main() {
    Signal.handle(Signal("INT")) {
        jsonSave()
        exitProcess(0)
    }
    val server = ServerSocket(8080)
    logger.info("Server is running on port ${server.localPort}")
    thread { console() }
    while (true) {
        val client = server.accept()
        logger.info("Client connected: ${client.inetAddress.hostAddress}")
        while (true) {
            try {
                val input = BufferedReader(InputStreamReader(client.inputStream))
                val json = input.readLine()
                val response = SecureManager.process(json)
                logger.info(response)
                val output = PrintWriter(client.getOutputStream(), true)
                output.println(response)
            } catch (e: Exception) {
                logger.info("Server lost connection, ${e.message}")
                break
            }
        }
    }
}

fun jsonSave(){
    try {
        JsonConvertor.save()
        logger.info("User: data saved")
    } catch(e: Exception){
        logger.error("Error: Can not save to json, ${e.message}")
    }
}

fun console(){
    val scan = BufferedReader(InputStreamReader(System.`in`))
    while (true) {
        print(">>> ")
        when (scan.readLine()) {
            "save" -> {
                jsonSave()
            }
            else -> {
                println("Я тебя не понял")
            }
        }
    }
}