package io

import entities.Coordinates
import entities.Difficulty
import entities.Discipline
import entities.LabWork
import io.Logger.logger
import java.math.BigInteger
import java.sql.Connection
import java.sql.DriverManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.Timestamp

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
            query.executeQuery()
        } catch (e: Exception) {
            return "${e.message}"
        }
        return "Try again"
    }

    fun checkUser(userName: String,
                  password: String): Long {
        try {
            val connection = connect()

            val enPassword = encryptString(password)

            val query = connection.prepareStatement("select * from users where username=? and password=? limit 1")
            query.setString(1, userName)
            query.setString(2, enPassword)
            val result = query.executeQuery()
            while (result.next()) {
                return if (result.getString("username") == userName
                        && result.getString("password") == enPassword) result.getLong("id")
                       else -1L
            }
        } catch (e: Exception) {
            return -1L
        }
        return -1L
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

    fun addLabWork(labWork: LabWork, userId: Long): Long {
        val connection = connect()

        val query = connection.prepareStatement("insert into data (name, coordinates_x, coordinates_y, minimal_point, difficulty, creation_date, discipline_name, discipline_self_study_hours, user_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?) returning id")
        query.setString(1, labWork.name)
        query.setLong(2, labWork.coordinates.x)
        query.setLong(3, labWork.coordinates.y)
        if (labWork.minimalPoint == null) query.setNull(4, java.sql.Types.BIGINT) else query.setLong(4, labWork.minimalPoint)
        if (labWork.difficulty == null) query.setNull(5, java.sql.Types.VARCHAR) else query.setString(5, labWork.difficulty.name)
        query.setTimestamp(6, Timestamp.from(labWork.creationDate.toInstant()))
        query.setString(7, labWork.discipline.name)
        query.setInt(8, labWork.discipline.selfStudyHours)
        query.setLong(9, userId)
        val result = query.executeQuery()
        while (result.next()) {
            return result.getLong("id")
        }
        return -1L
    }

    fun loadAll(): MutableList<LabWork> {
        val data = mutableListOf<LabWork>()

        val connection = connect()

        val query = connection.prepareStatement("select * from data")
        val result = query.executeQuery()
        while (result.next()){
            data.add(LabWork(result.getString("name"), Coordinates(result.getLong("coordinates_x"),
                result.getLong("coordinates_y")), if (result.getLong("minimal_point") == 0L) null else result.getLong("minimal_point"),
                if (result.getString("difficulty") == null) null else Difficulty.valueOf(result.getString("difficulty")), result.getDate("creation_date"),
                Discipline(result.getString("discipline_name"), result.getInt("discipline_self_study_hours"))
            ).apply { id = result.getLong("id") })
        }
        return data
    }

    private fun elemAccess(id: Long, userId: Long): String {
        val connection = connect()

        val query = connection.prepareStatement("select user_id from data where id=?")
        query.setLong(1, id)
        val result = query.executeQuery()
        result.next()
        if (userId != result.getLong("user_id")) return "Нет прав доступа"
        return "ok"
    }

    fun removeId(id: Long, userId: Long): String {
        var response = elemAccess(id, userId)
        if (response != "ok") {
            logger.info(response)
            return response
        }

        response = removeById(id)
        if (response != "ok") {
            logger.info(response)
            return "Error"
        }
        return "ok"
    }

    private fun removeById(id: Long): String {
        val connection = connect()

        val query = connection.prepareStatement("delete from data where id=?")
        query.setLong(1, id)
        query.executeUpdate()
        return "ok"
    }

    fun removeLast(userId: Long): Long {
        val connection = connect()

        val query = connection.prepareStatement("select max(id) from data where user_id=?")
        query.setLong(1, userId)
        val result = query.executeQuery()
        result.next()
        val lastId = result.getLong(1)
        val response = removeById(lastId)
        if (response != "ok") {
            logger.error("SQL Can't remove elem by id")
            return -1L
        }
        return lastId
    }

    fun update(labWork: LabWork,id: Long, userId: Long): String {
        val connection = connect()

        var response = elemAccess(id, userId)
        if (response != "ok") {
            logger.info(response)
            return response
        }
        val query = connection.prepareStatement("update data set name=?, coordinates_x=?, coordinates_y=?, minimal_point=?, difficulty=?, discipline_name=?, discipline_self_study_hours=?")
        query.setString(1, labWork.name)
        query.setLong(2, labWork.coordinates.x)
        query.setLong(3, labWork.coordinates.y)
        if (labWork.minimalPoint == null) query.setNull(4, java.sql.Types.BIGINT) else query.setLong(4, labWork.minimalPoint)
        if (labWork.difficulty == null) query.setNull(5, java.sql.Types.VARCHAR) else query.setString(5, labWork.difficulty.name)
        query.setString(6, labWork.discipline.name)
        query.setInt(7, labWork.discipline.selfStudyHours)
        query.executeUpdate()

        return "ok"
    }

    fun removeLower(mp: Long, userId: Long): String {
        val connection = connect()

        val query = connection.prepareStatement("delete from data where user_id=? and minimal_point<?")
        query.setLong(1, userId)
        query.setLong(2, mp)
        query.executeUpdate()
        return "ok"
    }

    fun removeByMP(mp: Long, userId: Long): String {
        val connection = connect()

        val query = connection.prepareStatement("delete from data where user_id=? and minimal_point=?")
        query.setLong(1, userId)
        query.setLong(2, mp)
        query.executeUpdate()
        return "ok"
    }

    fun clear(userId: Long): String {
        val connection = connect()

        val query = connection.prepareStatement("delete from data where user_id=?")
        query.setLong(1, userId)
        query.executeUpdate()
        return "ok"
    }
}