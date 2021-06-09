package com.studiocinqo.diardeonandroid.connect.container

import android.graphics.Bitmap
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface IPhoto {

    val id: String
    val filename: String
    val date: Date
    val owner: String?

    companion object {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        fun getPhoto(obj: JSONObject?): IPhoto? {
            return JWrap(obj)?.let { obj ->
                obj.getString("_id")?.let { id ->
                    obj.getString("filename")?.let { name ->
                        obj.getDate("date")?.let { date ->
                            val owner = obj.getString("owner")
                            return object : IPhoto {
                                override val id = id
                                override val filename = name
                                override val date = date
                                override val owner = owner
                            }
                        }
                    }
                }
            }
        }
    }
}

class PhotoAndBitmap(
    override val id: String,
    override val filename: String,
    val bitmap: Bitmap,
    override val date: Date,
    override val owner: String?
) : IPhoto{

}

