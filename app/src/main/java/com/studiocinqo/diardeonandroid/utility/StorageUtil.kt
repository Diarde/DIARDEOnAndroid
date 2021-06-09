package com.studiocinqo.diardeonandroid.utility

import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object StorageUtil {

    fun getStorageFile(type: FileType): File {
        val stamp = generateTimestamp()
        return when (type) {
            FileType.PICTURE -> {
                val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                //    .existOrCreate()
                File(file, "_$stamp.jpg")

            }
            FileType.VIDEO -> {
                val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                //    .existOrCreate()
                File(file, "_$stamp.mp4")
            }
        }
    }

    private fun generateTimestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.CHINA)
        return sdf.format(Date())
    }

    enum class FileType {
        PICTURE, VIDEO
    }
}