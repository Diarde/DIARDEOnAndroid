package com.studiocinqo.diardeonandroid.connect.container

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class JAWrap(val array: JSONArray?) {

    fun getStringList(): List<String> {
        return try {
            array?.let { array ->
                IntRange(0, array.length() - 1).mapNotNull {
                    array.getString(it)
                }
            } ?: listOf<String>()
        } catch (e: Exception) {
            listOf<String>()
        }
    }

    fun <T> getCustomList(converter: (obj: JSONObject?) -> T?): List<T> {
        return array?.let { array ->
            IntRange(0, array.length() - 1).mapNotNull {
                try {
                     converter(array[it] as JSONObject)
                } catch (e: Exception) {
                    null
                }
            }
        } ?: listOf<T>()

    }

    fun getArrayList(): List<JSONArray> {
        return array?.let { array ->
            IntRange(0, array.length() - 1).mapNotNull {
                try {
                    array[it] as JSONArray
                } catch (e: Exception) {
                    null
                }
            }
        } ?: listOf<JSONArray>()
    }

    fun getString(idx: Int): String? {
        return array?.let{
            return try { array.getString(idx) }
            catch(e: Exception) { null }
        }
    }

}