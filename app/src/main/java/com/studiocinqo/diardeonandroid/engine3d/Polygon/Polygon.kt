package com.studiocinqo.diardeonandroid.engine3d.Polygon

import com.studiocinqo.diardeonandroid.engine3d.Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

abstract class Polygon internal constructor(
    private val vertices: FloatArray,
    protected val indices: ShortArray,
    private val normals: FloatArray
){

    protected val vertexBuffer: FloatBuffer
    protected val drawListBuffer: ShortBuffer
    protected val normalsBuffer: FloatBuffer

    init{
        vertexBuffer = getVertexBuffer(vertices)
        drawListBuffer = getDrawListBuffer(indices)
        normalsBuffer = getNormalsBuffer(normals)
    }


    abstract fun Render(m: FloatArray?)

    companion object{

        fun Texture(texture: Texture,
                    vertices: FloatArray,
                    indices: ShortArray,
                    uvs: FloatArray,
                    normals: FloatArray): Polygon{
            return PolygonTexture(texture, vertices, indices, uvs, normals)
        }

        fun Color(vertices: FloatArray,
                  indices: ShortArray,
                  normals: FloatArray): Polygon{
            return PolygonColor(vertices, indices, normals)
        }

    }

    private fun getVertexBuffer(vertices: FloatArray): FloatBuffer {
        val vertexBuffer: FloatBuffer
        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        return vertexBuffer
    }

    private fun getNormalsBuffer(normals: FloatArray): FloatBuffer {
        val normalsBuffer: FloatBuffer
        val bb = ByteBuffer.allocateDirect(normals.size * 4)
        bb.order(ByteOrder.nativeOrder())
        normalsBuffer = bb.asFloatBuffer()
        normalsBuffer.put(normals)
        normalsBuffer.position(0)
        return normalsBuffer
    }


    private fun getDrawListBuffer(indices: ShortArray): ShortBuffer {
        val drawListBuffer: ShortBuffer
        val dlb = ByteBuffer.allocateDirect(indices.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(indices)
        drawListBuffer.position(0)
        return drawListBuffer
    }


}