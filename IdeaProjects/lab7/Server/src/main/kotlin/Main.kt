import datamanager.DataManager
import java.io.BufferedReader
import java.io.InputStreamReader
import datamanager.SecureManager
import io.Database
import java.io.PrintWriter
import java.net.ServerSocket
import io.Logger.logger

fun main() {
    val server = ServerSocket(8080)
    logger.info("Server is running on port ${server.localPort}")
    DataManager.load(Database.loadAll())
    while (true) {
        val client = server.accept()
        logger.info("Client connected: ${client.inetAddress.hostAddress}")
        while (true) {
            try {
                val input = BufferedReader(InputStreamReader(client.inputStream))
                val json = input.readLine()
                val response = SecureManager.process(json)
                logger.info(if (response == "ok") "Login successful" else response)
                val output = PrintWriter(client.getOutputStream(), true)
                output.println(response)
            } catch (e: Exception) {
                logger.info("Server lost connection, ${e.message}")
                break
            }
        }
    }
}
