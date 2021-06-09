package com.studiocinqo.diardeonandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.ui.fragments.result.model.ModelFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Model3DActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_model3_d)

    }

}

