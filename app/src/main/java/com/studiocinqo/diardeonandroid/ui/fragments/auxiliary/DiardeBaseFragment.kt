package com.studiocinqo.diardeonandroid.ui.fragments.auxiliary

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.connect.utility.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class DiardeBaseFragment: Fragment() {

    protected fun handleError(error: Int, activity: FragmentActivity){
        if(error == Status.NOTAUTHENTICATED.code) {
            GlobalScope.launch(Dispatchers.Main) {
                    APIAuthentication.autoLogin(activity).onError { error ->
                        when(error){
                            Status.NOTAUTHENTICATED.code -> findNavController().navigate(R.id.loginFragment)
                            Status.NOCONNECTION.code -> displayNoConnectionModal(activity)
                        }}
                }
            }
        if(error == Status.NOCONNECTION.code ) displayNoConnectionModal(activity)
    }

    private fun displayNoConnectionModal(activity: FragmentActivity){
        NotificationModalFragment(R.string.no_connection, R.string.ok) {}.run {
                show(
                    activity.supportFragmentManager,
                    "NotificationFragment"
                )
        }
    }
    }

