package com.studiocinqo.diardeonandroid.engine3d.modelbuilder

import com.studiocinqo.diardeonandroid.engine3d.GLRenderer
import com.studiocinqo.diardeonandroid.engine3d.Polygon.Polygon
import com.studiocinqo.diardeonandroid.engine3d.Vector3D
import earcut4j.Earcut

class Ground(val renderer: GLRenderer,
             val vertices: HashMap<String, Vector3D>) {

    fun addFloor(ground: List<String>) {
        ground.mapNotNull{ vertices.get(it) }.run{
            Earcut.earcut(
                flatMap { listOf(it.X, it.Z) }.toDoubleArray(),
                intArrayOf(), 2
            ).let { _indices ->
                val _uvs = flatMap { it.run { listOf(X.toFloat() / 4f, Z.toFloat() / 4f) } }
                val _vertices = flatMap {
                    it.run {
                        listOf(
                            X.toFloat() / 10f,
                            Y.toFloat() / 10f,
                            -Z.toFloat() / 10f
                        )
                    }
                }
                val _normals = ground.flatMap { it.run { listOf(0f, 1f, 0f) } }
                renderer.run {
                    Polygon.Texture(
                        textureMap.Ground, _vertices.toFloatArray(),
                        _indices.map { it.toShort() }.toShortArray(), _uvs.toFloatArray(),
                        _normals.toFloatArray()
                    ).let {
                        addPolygon(it)
                    }
                }
            }
        }
    }

}