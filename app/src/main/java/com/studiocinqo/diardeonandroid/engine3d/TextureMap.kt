package com.studiocinqo.diardeonandroid.engine3d

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class TextureMap(context: Context) {

    val Ground: Texture
    val Wall: Texture

    init{
        Ground = Texture(0, BitmapFactory
            .decodeStream(context.assets.open("images/floor.jpg")))

        Wall = Texture(1, BitmapFactory
            .decodeStream(context.assets.open("images/wall.jpg")))
    }



}

class Texture(val lot: Int,val bmp: Bitmap) {
}