package com.studiocinqo.diardeonandroid.ui.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.connect.APIGeometry
import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.engine3d.GLRenderer
import com.studiocinqo.diardeonandroid.engine3d.modelbuilder.Modelbuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.PI

class ModelView : GLSurfaceView {

    private val TOUCH_SCALE_FACTOR: Float = 180.0f / 640f
    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private var mScaleFactor: Float = 1f
    private val gestureDetector: GestureDetectorCompat
    private val scaleDetector: ScaleGestureDetector

    private val renderer: GLRenderer
    private val modelBuilder: Modelbuilder

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    init {
        setEGLContextClientVersion(2)
        renderer = GLRenderer(context)
        setRenderer(renderer)

        modelBuilder = Modelbuilder(renderer)

        gestureDetector = getGestureDetector()
        scaleDetector = getScaleDetector()

    }

    fun setModel(model: IModel.IData.IModel){
        modelBuilder.update(model)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)
        return true
    }

    private fun getGestureDetector(): GestureDetectorCompat {
        return GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                renderer.alpha -= distanceX * TOUCH_SCALE_FACTOR
                renderer.beta -= distanceY * TOUCH_SCALE_FACTOR * PI.toFloat() / 180f
                requestRender()
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })
    }

    private fun getScaleDetector(): ScaleGestureDetector {
       return  ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    mScaleFactor *= detector.scaleFactor
                    mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))
                    renderer.scale = mScaleFactor
                    requestRender()
                    //invalidate()
                    return true
                }
            })
    }



}