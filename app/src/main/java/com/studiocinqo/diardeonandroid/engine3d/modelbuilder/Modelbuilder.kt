package com.studiocinqo.diardeonandroid.engine3d.modelbuilder

import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.engine3d.*

class Modelbuilder(private val renderer: GLRenderer) {

    init {

    }

    fun update(model: IModel.IData.IModel) {

        val vertexMap = getVertexMap(model)
        val displacementMap = Displacements.getDisplacementsMap(model, vertexMap)

        Ground(renderer, vertexMap).run {

            addFloor(model.ground)

        }

        Wall(renderer, vertexMap, displacementMap).run {

            model.faces.forEach { face ->
                try {
                    addWall(face.vertices, face.windows, face.doors)
                } catch (e: Exception) {
                    Log.d("ModelBuilder", "error adding Wall")
                }
            }
        }
    }


    /** Aux functions **/

    private fun getFacesForVertex(vertex: String): List<String> {
        return listOf()
    }

    private fun getVertexMap(model: IModel.IData.IModel): HashMap<String, Vector3D> {
        return hashMapOf<String, Vector3D>().also { map ->
            model.vertices.forEach { vertex ->
                map.put(
                    vertex.id,
                    Vector3D(
                        vertex.point.x,
                        vertex.point.y,
                        vertex.point.z
                    )
                )
            }
        }
    }

}

