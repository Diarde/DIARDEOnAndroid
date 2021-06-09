package com.studiocinqo.diardeonandroid.connect

import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IProject
import com.studiocinqo.diardeonandroid.connect.container.IResult
import com.studiocinqo.diardeonandroid.connect.container.JWrap
import org.json.JSONObject

class APIProject {

    companion object {

        suspend fun postProject(name: String, description: String): IResult<String>{
            return APICore.instance.post("/_api/projects", JSONObject().apply{
                put("name", name)
                put("description", description)
            }).map{ it ->
                JWrap(it).getString("id")
            }
        }

        suspend fun getProjects(): IResult<List<IProject>> {
            Log.d("APIProject", "getProjects")
            return APICore.instance.getAsJArray("/_api/projects").
                map { array ->
                    IntRange(0, array.length() - 1).mapNotNull {
                        IProject.getProject(array[it] as JSONObject)
                    }
                }
        }

        suspend fun getProject(id: String): IResult<IProject> {
            return APICore.instance.getAsJSON("/_api/projects/${id}").map {
                IProject.getProject(it)
            }
        }

        suspend fun deleteProject(id: String): IResult<JSONObject> {
            return APICore.instance.delete("/_api/projects/${id}")
        }

        suspend fun updateProject(
            id: String,
            name: String,
            description: String
        ): IResult<IProject> {
            return APICore.instance.put("/_api/projects/${id}",
                JSONObject()?.also {
                    it.put("name", name)
                    it.put("description", description)
                }).map{
                IProject.getProject(it)
            }
        }


    }


}