package com.studiocinqo.diardeonandroid.connect.container

import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class JWrap(val obj: JSONObject?) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    fun getString(key: String): String? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getString(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDouble(key: String): Double? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getDouble(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getBoolean(key: String): Boolean? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getBoolean(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getInt(key: String): Int? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getInt(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getArray(key: String): JSONArray? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getJSONArray(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getObject(key: String): JSONObject? {
        return try {
            return obj?.let {
                if (obj.has(key)) obj.getJSONObject(key) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDate(key: String): Date? {
        return try {
            return obj?.let {
                if (obj.has(key)) dateFormat.parse((obj).getString(key)) else null
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object{

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        fun dateToString(date: Date): String{
            return "\"${dateFormat.format(date)}\""
        }

    }

}