package com.studiocinqo.diardeonandroid.enginecanvas

import android.graphics.Canvas
import com.studiocinqo.diardeonandroid.enginecanvas.primitives.PrimitiveBase

class CanvasEngine() {

    var zoom: Float = 1f
    var offsetX: Float = 0f
    var offsetY: Float = 0f

    var primitives: HashSet<PrimitiveBase> = hashSetOf()

    fun addPrimitive(primitive: PrimitiveBase){
         primitives.add(primitive)
    }

    fun removePrimitive(primitive: PrimitiveBase){
        primitives.remove(primitive)
    }

    fun onDraw(canvas: Canvas){
        primitives.forEach { primitive ->
            primitive.onDraw(this, canvas)
        }
    }

}