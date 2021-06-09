package com.studiocinqo.diardeonandroid.engine3d

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.studiocinqo.diardeonandroid.engine3d.Polygon.Polygon
import java.util.concurrent.ConcurrentLinkedQueue
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class GLRenderer(  // Misc
    var mContext: Context
) : GLSurfaceView.Renderer {
    // Our matrices
    private val mtrxRotation = FloatArray(16)
    private val mtrxProjection = FloatArray(16)
    private val mtrxView = FloatArray(16)
    private val mtrxProjectionAndView = FloatArray(16)
    private val mtrxProjectionAndViewAndRotation = FloatArray(16)
    val textureMap: TextureMap = TextureMap(mContext)
    val shaders = GLShaders(mContext)

    private val wall: Polygon
    private val floor: Polygon

    private val queue =  ConcurrentLinkedQueue<Polygon>()
    private val polygons: MutableList<Polygon> = mutableListOf()

    public var alpha = 0.0f
    public var beta = 0.0f
    public var scale = 1f

    var mLastTime: Long
    var mProgram = 0

    fun onPause() {
    }

    fun onResume() {
        mLastTime = System.currentTimeMillis()
    }

    override fun onDrawFrame(unused: GL10) {
        val now = System.currentTimeMillis()
        if (mLastTime > now) return
        val elapsed = now - mLastTime

        Matrix.setRotateM(mtrxRotation, 0, alpha, 0f, 1.0f, 0.0f)
        Matrix.setLookAtM(
            mtrxView, 0, 0f, sin(beta) *3f / scale, -cos(beta)*3f / scale, 0f,
            0.0f, 0f, 0f, 1.0f, 0.0f
        )
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0)
        Matrix.multiplyMM(
            mtrxProjectionAndViewAndRotation,
            0,
            mtrxProjectionAndView,
            0,
            mtrxRotation,
            0
        )
        Render(mtrxProjectionAndViewAndRotation)

        repeat(5) {
            queue.poll()?.run {
                polygons.add(this)
            }
        }


        mLastTime = now
    }

    private fun Render(m: FloatArray) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //sprite.Render(m)
        //polygon.Render(m)
        polygons.forEach {
            it.Render(m)
         }

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(mtrxProjection, 0, -ratio/4, ratio/4, -0.2f, 0.3f, 1f, 40f)

    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        SetupTextures()
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        var vertexShader = shaders.loadShader(
            GLES20.GL_VERTEX_SHADER,
            shaders.SolidColorVertexShader
        )
        var fragmentShader = shaders.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            shaders.SolidColorFragmentShader
        )
        GLShaders.sp_SolidColor = GLES20.glCreateProgram()
        GLES20.glAttachShader(GLShaders.sp_SolidColor, vertexShader)
        GLES20.glAttachShader(GLShaders.sp_SolidColor, fragmentShader)
        GLES20.glLinkProgram(GLShaders.sp_SolidColor)
        vertexShader = shaders.loadShader(
            GLES20.GL_VERTEX_SHADER,
            shaders.TextureVertexShader
        )
        fragmentShader = shaders.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            shaders.TextureFragmentShader
        )
        GLShaders.sp_Image = GLES20.glCreateProgram()
        GLES20.glAttachShader(GLShaders.sp_Image, vertexShader)
        GLES20.glAttachShader(GLShaders.sp_Image, fragmentShader)
        GLES20.glLinkProgram(GLShaders.sp_Image)
        GLES20.glUseProgram(GLShaders.sp_Image)
    }

    fun SetupTextures() {
        val texturenames = IntArray(1)
        GLES20.glGenTextures(1, texturenames, 0)

        textureMap.let{ map ->
            listOf(map.Ground, map.Wall).forEach { texture ->
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + texture.lot)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.lot)
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
                )
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture.bmp, 0)
            }
        }

    }

    init {
        mLastTime = System.currentTimeMillis() + 100

        wall = Polygon.Texture(textureMap.Wall,
            floatArrayOf(
                -0.2f, -0.2f, -0f, 0.2f, -0.2f, -0f,
                -0.2f, -0.2f, 0f, -0.2f, 0.2f, 0f
            ),
            shortArrayOf(0, 1, 2, 0, 2, 3),
            floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f),
            floatArrayOf(1f,0f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f))

        floor = Polygon.Texture(
            textureMap.Ground,
            floatArrayOf(
                -0.2f, -0.2f, -0.2f, 0.2f, -0.2f, -0.2f,
                0.2f, -0.2f, 0.2f, -0.2f, -0.2f, 0.2f
            ),
            shortArrayOf(0, 1, 2, 0, 2, 3),
            floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f),
            floatArrayOf(1f,0f,0f,1f,0f,0f,1f,0f,0f,1f,0f,0f)
        )
    }

    fun addPolygon(polygon: Polygon){
        queue.offer(polygon)
    }
}