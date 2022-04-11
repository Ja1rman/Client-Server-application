import io.CommandResolver
import io.CommandResolver.scan
import io.ServerConnection
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    ServerConnection.connect()
    while (true) {
        print(">>> ")
        val auth = scan.readLine().split(" ")
        try {
            if (auth[0] == "/reg") {
                val response = ServerConnection.reg(auth[1], auth[2])

                if (response == "Запрос не вернул результатов.") {
                    println("Регистрация прошла успешно")
                    break
                }
                println(response)
            } else if (auth[0] == "/l") {
                val response = ServerConnection.login(auth[1], auth[2])
                if (response == "ok") {
                    println("Авторизация прошла успешно")
                    break
                }
                println(response)
            } else if (auth[0] == "exit") {
                ServerConnection.client.close()
                return
            } else {
                println("Авторизуйтесь через /l <userName> <password> или Зарегистрируйтесь через /reg <userName> <password>")
            }
        } catch (e: Exception) {
            println("Неверный формат данных! Error: ${e.message}")
        }
    }


    while (true) {
        print(">>> ")
        var commands = scan.readLine() ?: "-1a"
        if (commands == "-1a") {
            scan = BufferedReader(InputStreamReader(System.`in`))
            commands = scan.readLine()
        }
        val command = commands.split(" ")
        CommandResolver.resolve(command)
        if (command[0] == "exit") break
    }
}




