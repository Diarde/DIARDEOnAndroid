package com.studiocinqo.diardeonandroid.enginecanvas.primitives

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine


class Polygon(private val xs: FloatArray, private val ys: FloatArray, private val color: String): PrimitiveBase() {

    private val paint: Paint  = Paint()
    private val path: Path = Path()

    init{
        paint.color = Color.parseColor(color)
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL

        setPath(path, xs, ys)
    }

    override fun onDraw(engine: CanvasEngine, canvas: Canvas) {
        canvas?.let{ canvas ->
            canvas.drawPath(path, paint)
        }
    }

    private fun setPath(path: Path, xs: FloatArray, ys: FloatArray) {
        path.reset()
        xs.zip(ys).let { xys ->
            path.moveTo(xys.last().first, xys.last().second)
            xys.forEach { xy ->
                path.lineTo(xy.first, xy.second)
            }
        }
    }

}