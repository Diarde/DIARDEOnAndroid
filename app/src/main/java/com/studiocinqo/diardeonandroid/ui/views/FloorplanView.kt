package com.studiocinqo.diardeonandroid.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine
import com.studiocinqo.diardeonandroid.enginecanvas.floorplanbuilder.Floorplanbuilder

class FloorplanView : View {

    val engine: CanvasEngine = CanvasEngine()

    var mScaleFactor = 1f
    var mScrollX = 0f
    var mScrollY = 0f

    private lateinit var textPaint: TextPaint

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                Log.d("TAG", distanceX.toString())
                mScrollX -= distanceX / mScaleFactor
                mScrollY -= distanceY / mScaleFactor
                invalidate()
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })?.let { gestureDetector ->
            ScaleGestureDetector(context,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        mScaleFactor *= detector.scaleFactor
                        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f))
                        invalidate()
                        return true
                    }
                })?.let { scaleDetector ->
                this.setOnTouchListener(
                    object : OnTouchListener {
                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                            gestureDetector.onTouchEvent(event)
                            scaleDetector.onTouchEvent(event)
                            return true
                        }

                    }
                )
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(width / 2f, height / 2f)
        canvas.translate(mScrollX * mScaleFactor, mScrollY * mScaleFactor)
        canvas.scale(mScaleFactor, mScaleFactor)
        engine.onDraw(canvas)
        canvas.restore()
    }
}