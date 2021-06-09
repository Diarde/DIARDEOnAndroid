package com.studiocinqo.diardeonandroid.enginecanvas.primitives

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine

class Line(private val x1: Float, private val y1: Float,
           private val x2: Float, private val y2: Float, private val color: String): PrimitiveBase() {

    private val paint: Paint  = Paint()

    init{
        paint.setColor(Color.parseColor(color));
        paint.setStrokeWidth(3f);
    }

    override fun onDraw(engine: CanvasEngine, canvas: Canvas) {
        canvas?.let{canvas ->
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }

}