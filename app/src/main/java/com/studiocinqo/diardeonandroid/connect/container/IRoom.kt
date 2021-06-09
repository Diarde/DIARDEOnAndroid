package com.studiocinqo.diardeonandroid.connect.container

import org.json.JSONObject
import java.util.*

interface IRoom {

    val id: String
    val name: String
    val description: String?
    val date: Date?
    val photos: List<IPhoto>
    val supplements: List<IPhoto>?
    val processing: IProcessing?

    companion object {

        fun getRoom(obj: JSONObject?): IRoom? {
            return JWrap(obj)?.let{ obj ->
                obj.getString("_id")?.let{id ->
                    obj.getString("name")?.let{name ->
                            obj.getArray("fotos")?.let{photos -> JAWrap(photos).getCustomList { obj -> IPhoto.getPhoto(obj) }}?.let{photos ->
                                val date = obj.getDate("date")
                                val description = obj.getString("description") ?: ""
                                val supplements = obj.getArray("supplements")?.let{photos -> JAWrap(photos).getCustomList { obj -> IPhoto.getPhoto(obj) }}
                                val processing = obj.getObject("processing")?.let{processing -> IProcessing.getProcessing(processing)}
                                return object : IRoom {
                                    override val id = id
                                    override val name = name
                                    override val description = description as String
                                    override val date = date
                                    override val photos = photos
                                    override val supplements = supplements
                                    override val processing = processing
                                }
                            }
                    }
                }
            }

        }
    }

}

enum class Status(val st: Int){
    OPEN(0),
    PENDING(1),
    PROCESSED(2);

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.st == value }
    }

}

interface IProcessing{
    val status: Status

    companion object {
        fun getProcessing(obj: JSONObject): IProcessing? {
            return JWrap(obj)?.let{obj ->
                obj.getInt("status")?.let{ value ->
                    Status.getByValue(value)?.let{ status ->
                        object: IProcessing{
                            override val status = status
                        }
                    }
                }
            }
        }
    }
}

