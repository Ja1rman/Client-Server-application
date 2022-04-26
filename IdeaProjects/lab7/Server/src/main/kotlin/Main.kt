import datamanager.DataManager
import java.io.BufferedReader
import java.io.InputStreamReader
import datamanager.SecureManager
import io.Database
import java.io.PrintWriter
import java.net.ServerSocket
import io.Logger.logger
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import kotlin.concurrent.thread


fun main() = runBlocking {
    val server = ServerSocket(8080)
    logger.info("Server is running on port ${server.localPort}")

    val scope = CoroutineScope(Dispatchers.Default)
    val scopeCached = CoroutineScope(Executors.newCachedThreadPool().asCoroutineDispatcher())
    val scopeFixed = CoroutineScope(newFixedThreadPoolContext(4, "syncPool"))

    DataManager.load(Database.loadAll())
    while (true) {
        val client = server.accept()
        logger.info("Client connected: ${client.inetAddress.hostAddress}")

        scope.launch {
            try {
                while (true) {
                    val input = BufferedReader(InputStreamReader(client.inputStream))
                    val json = input.readLine()

                    scopeCached.launch {
                        try {
                            val response = SecureManager.process(json)
                            logger.info(if (response == "ok") "Login successful" else response)
                            val output = PrintWriter(client.getOutputStream(), true)

                            scopeFixed.launch {
                                try {
                                    output.println(response)
                                } catch (e: Exception) {
                                    logger.info("Server lost connection3, ${e.message}")
                                    scopeFixed.cancel()
                                    scopeCached.cancel()
                                    scope.cancel()
                                }
                            }.start()
                        } catch (e: Exception) {
                            logger.info("Server lost connection2, ${e.message}")
                            scopeCached.cancel()
                            scope.cancel()
                        }
                    }.start()
                }
            } catch (e: Exception) {
                logger.info("Server lost connection, ${e.message}")
                scope.cancel()
            }
        }.start()

    }
}