package com.studiocinqo.diardeonandroid.engine3d.Polygon

import android.opengl.GLES20
import com.studiocinqo.diardeonandroid.engine3d.GLShaders

class PolygonColor internal constructor(
                                           vertices: FloatArray,
                                           indices: ShortArray,
                                           normals: FloatArray): Polygon(
    vertices, indices, normals){


    override fun Render(m: FloatArray?) {


        //get handle to vertex shader's vPosition member
        val mPositionHandle: Int = GLES20.glGetAttribLocation(
            GLShaders.sp_SolidColor,
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
            GLShaders.sp_SolidColor,
            "vNormal"
        )

        // Enable generic normals attribute array
        GLES20.glEnableVertexAttribArray(mNormalLoc)

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer(
            mNormalLoc, 3, GLES20.GL_FLOAT, false, 0,
            normalsBuffer
        )

        // Get handle to shape's transformation matrix
        val mtrxhandle: Int = GLES20.glGetUniformLocation(
            GLShaders.sp_SolidColor,
            "uMVPMatrix"
        )

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0)

        // Draw the triangle
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, indices.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mNormalLoc)
    }


}