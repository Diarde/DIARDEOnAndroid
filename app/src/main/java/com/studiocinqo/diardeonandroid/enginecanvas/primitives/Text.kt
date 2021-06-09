package com.studiocinqo.diardeonandroid.enginecanvas.primitives

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine

class Text(private val text: String, private val x: Float, private val y: Float,
           private val rotation: Float, val _color: String): PrimitiveBase()  {


    val textPaint: TextPaint
    val textWidth: Float
    val textHeight: Float

    init{
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = 14f
            color = Color.parseColor(_color)
        }.apply{
            textWidth = measureText(text)
            textHeight = fontMetrics.bottom
        }
    }

    override fun onDraw(engine: CanvasEngine, canvas: Canvas) {

        canvas.save()

        canvas.rotate(rotation, x, y)

        canvas.drawText(text, x, y, textPaint)

        canvas.restore()

    }


}