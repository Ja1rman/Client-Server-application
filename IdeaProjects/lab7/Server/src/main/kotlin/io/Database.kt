package io

import entities.LabWork
import java.math.BigInteger
import java.sql.Connection
import java.sql.DriverManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Database {
    private fun connect(): Connection {
        val jdbcUrl = "jdbc:postgresql://localhost:5432/studs"
        return DriverManager.getConnection(jdbcUrl, "s335072", "gld281")
    }
    fun addUser(userName: String,
                password: String): String{
        try {
            val connection = connect()

            val enPassword = encryptString(password)

            val query = connection.prepareStatement("insert into users (username, password) values (?, ?)")
            query.setString(1, userName)
            query.setString(2, enPassword)
            val result = query.executeQuery()
        } catch (e: Exception) {
            return "${e.message}"
        }
        return "Try again"
    }

    fun checkUser(userName: String,
                  password: String): String{
        try {
            val connection = connect()

            val enPassword = encryptString(password)

            val query = connection.prepareStatement("select * from users where username=? and password=? limit 1")
            query.setString(1, userName)
            query.setString(2, enPassword)
            val result = query.executeQuery()
            while (result.next()){
                return if (result.getString("username") == userName
                        && result.getString("password") == enPassword) "ok"
                       else "Неверные данные"
            }
        } catch (e: Exception) {
            return "Error: ${e.message}"
        }
        return "Неверные данные"
    }

    fun saveData(user_id: String, data: MutableList<LabWork>): String{
        val connection = connect()

        for (item in data) {
            val query = connection.prepareStatement("insert into data (name, coordinates_x, coordinates_y, minimal_point, difficulty, creation_date, discipline_name, discipline_self_study_hours, user_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)")
            query.setString(1, item.name)
            val result = query.executeQuery()
        }
    }

    private fun encryptString(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-512")
            val messageDigest = md.digest(input.toByteArray())
            val no = BigInteger(1, messageDigest)
            var hashtext = no.toString(16)
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}