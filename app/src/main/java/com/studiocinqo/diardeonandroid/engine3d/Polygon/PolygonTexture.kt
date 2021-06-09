package com.studiocinqo.diardeonandroid.engine3d.Polygon

import android.opengl.GLES20
import com.studiocinqo.diardeonandroid.engine3d.GLShaders
import com.studiocinqo.diardeonandroid.engine3d.Texture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class PolygonTexture  internal constructor(private val texture: Texture,
                                           vertices: FloatArray,
                                           indices: ShortArray,
                                           private val uvs: FloatArray,
                                           normals: FloatArray): Polygon(
                                            vertices, indices, normals){


    private val uvBuffer = getUVBuffer(uvs)

    override fun Render(m: FloatArray?) {


        //get handle to vertex shader's vPosition member
        val mPositionHandle: Int = GLES20.glGetAttribLocation(
            GLShaders.sp_Image,
            "vPosition"
        )

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mPositionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            mPositionHandle, 3, GLES20.GL_FLOAT, false,
            0, vertexBuffer
        )

        val mNormalLoc: Int = GLES20.glGetAttribLocation(
            GLShaders.sp_Image,
            "vNormal"
        )

        // Enable generic normals attribute array
        GLES20.glEnableVertexAttribArray(mNormalLoc)

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(
            mNormalLoc, 3, GLES20.GL_FLOAT, false, 0,
            normalsBuffer
        )


        // Get handle to texture coordinates location
        val mTexCoordLoc: Int = GLES20.glGetAttribLocation(
            GLShaders.sp_Image,
            "a_texCoord"
        )

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray(mTexCoordLoc)

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(
            mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0,
            uvBuffer
        )

        // Get handle to shape's transformation matrix
        val mtrxhandle: Int = GLES20.glGetUniformLocation(
            GLShaders.sp_Image,
            "uMVPMatrix"
        )

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0)

        // Get handle to textures locations
        val mSamplerLoc: Int = GLES20.glGetUniformLocation(
            GLShaders.sp_Image,
            "s_texture"
        )

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i(mSamplerLoc, texture.lot)
        val mOffset: Int = GLES20.glGetUniformLocation(
            GLShaders.sp_Image,
            "f_offset"
        )
        GLES20.glUniform1f(mOffset, 0f)

        // Draw the triangle
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, indices.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordLoc)
        GLES20.glDisableVertexAttribArray(mNormalLoc)
    }

    private fun getUVBuffer(uvs: FloatArray): FloatBuffer {
        val uvBuffer: FloatBuffer
        val bb = ByteBuffer.allocateDirect(uvs.size * 4)
        bb.order(ByteOrder.nativeOrder())
        uvBuffer = bb.asFloatBuffer()
        uvBuffer.put(uvs)
        uvBuffer.position(0)
        return uvBuffer
    }

}