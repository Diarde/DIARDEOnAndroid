package com.studiocinqo.diardeonandroid.connect

import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IResult
import com.studiocinqo.diardeonandroid.connect.container.IRoom
import com.studiocinqo.diardeonandroid.connect.container.JWrap
import org.json.JSONObject

class APIRoom {

    companion object {

        suspend fun getRooms(projectID: String): IResult<List<IRoom>> {
            Log.d("APIRoom", "getRooms")
            return APICore.instance.getAsJArray(
                "/_api/projects/${projectID.toString()}/rooms",
            ).map { array ->
                IntRange(0, array.length() - 1).mapNotNull {
                    IRoom.getRoom(array[it] as JSONObject)
                }
            }
        }

        suspend fun getRoom(projectID: String, roomId: String): IResult<IRoom> {
            return APICore.instance.getAsJSON(
                "/_api/projects/${projectID.toString()}/rooms/${roomId.toString()}",
            ).map { obj ->
                IRoom.getRoom(obj)
            }
        }

        suspend fun postRoom(projectId: String, name: String, description: String): IResult<String> {
            return APICore.instance.post("/_api/projects/${projectId}/rooms",
                JSONObject()?.also {
                    it.put("name", name)
                    it.put(description, description)
                }).map { obj ->
                JWrap(obj).getString("id")
                 }
        }

        suspend fun deleteRoom(projectId: String, roomId: String): IResult<JSONObject> {
            return APICore.instance.delete("/_api/projects/${projectId}/rooms/${roomId}")
        }

        suspend fun updateRoom(
            projectId: String, roomId: String,
            name: String, description: String
        ): IResult<IRoom> {
            return APICore.instance.put(
                "/_api/projects/${projectId}/rooms/${roomId}",
                JSONObject()?.also {
                    it.put("name", name)
                    it.put("description", description)
                }).map { obj ->  IRoom.getRoom(obj) }
        }

        suspend fun requestModels(projectID: String, roomID: String,
                                  includeSKP: Boolean, includeDXF: Boolean): IResult<Any>{
            return APICore.instance.post(
                "/_api/projects/${projectID}/rooms/${roomID}/requestmodels",
                JSONObject()?.also {
                    it.put("skp", includeSKP)
                    it.put("dxf", includeDXF)
                })
        }

        suspend fun submitPhotos(projectId: String, roomId: String): IResult<IRoom> {
            return APICore.instance.post(
                "/_api/projects/${projectId}/rooms/${roomId}/submit",
                JSONObject()).map { obj ->
                IRoom.getRoom(obj)
            }
        }

    }

}


