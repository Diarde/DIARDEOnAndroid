package com.studiocinqo.diardeonandroid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.ui.fragments.camera.CameraKitFragment

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CameraKitFragment.newInstance())
                .commit()
        }
    }
}