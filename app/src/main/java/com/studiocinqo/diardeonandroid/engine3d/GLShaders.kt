package com.studiocinqo.diardeonandroid.engine3d

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader

class GLShaders(context: Context) {

    val SolidColorVertexShader =
        context.assets.open("shaders/solidcolor.vsh").bufferedReader().use(BufferedReader::readText)

    val SolidColorFragmentShader =
        context.assets.open("shaders/solidcolor.fsh").bufferedReader().use(BufferedReader::readText)

    val TextureVertexShader =
        context.assets.open("shaders/texture.vsh").bufferedReader().use(BufferedReader::readText)

    val TextureFragmentShader =
        context.assets.open("shaders/texture.fsh").bufferedReader().use(BufferedReader::readText)

    companion object {
        var sp_SolidColor = 0
        var sp_Image = 0
    }

    public fun loadShader(type: Int, shaderCode: String?): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // return the shader
        return shader
    }

}