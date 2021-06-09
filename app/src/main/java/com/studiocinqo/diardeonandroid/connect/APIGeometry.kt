package com.studiocinqo.diardeonandroid.connect

import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.connect.container.IResult
import com.studiocinqo.diardeonandroid.connect.container.JWrap
import org.json.JSONObject

class APIGeometry {

    companion object {

        suspend fun loadModel(projectID: String, roomID: String): IResult<IModelWrapper> {
            return APICore.instance.getAsJSON(
                "/_api/projects/${projectID}/rooms/${roomID}/model",
            ).map { obj ->
                IModelWrapper.getModel(obj)
            }
        }
    }

}

class IModelWrapper(    val name: String,
                        val dxf: Boolean,
                        val skp: Boolean,
                        val model: IModel) {

    companion object {

        fun getModel(obj: JSONObject): IModelWrapper? {
            return JWrap(obj)?.let { obj ->
                obj.getString("name")?.let { name ->
                    obj.getBoolean("dxf")?.let { dxf ->
                        obj.getBoolean("skp")?.let { skp ->
                            obj.getObject("model")?.let { obj ->
                                IModel.getModel(obj)?.let { model ->
                                    IModelWrapper ( name,dxf,skp, model
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun toString(): String{
        return "{name: ${name}, dxf: ${dxf}, skp: ${skp}, model: ${model.toString()}}"
    }

}

