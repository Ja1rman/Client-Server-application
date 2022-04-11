package io
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import datamanager.DataManager
import java.io.BufferedOutputStream
import java.io.FileOutputStream;

/**
 * Object to serialize objects to json strings and save
 */
object JsonConvertor {
    private val mapper = jacksonObjectMapper()
    /**
     * Do not takes args. Info to save takes from object DataManager
     */
    fun save() {
        val json = mapper.writeValueAsString(DataManager)
        val file = FileOutputStream(System.getenv("LABVAR"))
        val output = BufferedOutputStream(file)
        val array = json.toByteArray()

        output.write(array)
        output.close()
    }
}