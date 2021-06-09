package com.studiocinqo.diardeonandroid.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.run{
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.toolbar_default)
        }

        window.setStatusBarColor(getColorFromAttr(R.attr.background))

        /*val intent = Intent(this, Model3DActivity::class.java).apply {
            // putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)*/

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                APIAuthentication.clearCredentials(this)
                GlobalScope.launch(Dispatchers.Main) { APIAuthentication.logout() }
                findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    @ColorInt
    fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

}