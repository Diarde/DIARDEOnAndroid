package com.studiocinqo.diardeonandroid.enginecanvas.primitives

import android.graphics.Canvas
import com.studiocinqo.diardeonandroid.enginecanvas.CanvasEngine

abstract class PrimitiveBase() {

    abstract fun onDraw(engine: CanvasEngine, canvas: Canvas)

    fun bind(engine: CanvasEngine){
        engine.addPrimitive(this)
    }

    fun unbind(engine: CanvasEngine){
        engine.removePrimitive(this)
    }

}