package com.studiocinqo.diardeonandroid.connect.container

import org.json.JSONObject
import java.util.*

interface IProject {

    val id: String
    val name: String
    val description: String?
    val date: Date
    val rooms: List<IRoom>?

    companion object {

        fun getProject(obj: JSONObject?): IProject? {
            return JWrap(obj)?.let{obj ->
                obj.getString("_id")?.let{id ->
                    obj.getString("name")?.let{name ->
                        obj.getDate("date")?.let { date ->
                            val description = obj.getString("description")
                            val rooms = obj.getArray("rooms")?.let{array ->
                                    JAWrap(array)?.getCustomList { obj -> IRoom.getRoom(obj) }
                                }
                            return object : IProject {
                                override val id = id
                                override val name = name
                                override val description = description
                                override val date = date
                                override val rooms = rooms

                            }
                        }
                    }
                }
            }
        }
    }
}