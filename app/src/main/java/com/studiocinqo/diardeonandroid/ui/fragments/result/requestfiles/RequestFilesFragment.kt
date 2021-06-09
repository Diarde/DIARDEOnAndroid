package com.studiocinqo.diardeonandroid.ui.fragments.result.requestfiles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIRoom
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.NotificationModalFragment
import com.studiocinqo.diardeonandroid.ui.fragments.result.ARG_HASSKP
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class RequestFilesFragment : Fragment() {
    private var projectID: String? = null
    private var roomID: String? = null

    private var requestButton: Button? = null

    private var requestSKP = false
    private var requestDXF = false

    private var hasSKP = false
    private var hasDXF = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
            hasSKP = it.getBoolean(ARG_HASSKP) ?: false
            hasDXF = it.getBoolean(ARG_HASSKP) ?: false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.run {
            supportActionBar?.run {
                setCustomView(R.layout.toolbar_default)
                findViewById<TextView>(R.id.actionbarDefaultTitle)?.run {
                    text = getString(R.string.requestfiles)
                }
                show()
            }
        }

        return inflater.inflate(R.layout.fragment_request_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener{
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.buttonRequest).let{
            requestButton = it
            toggleButton(evaluateForToggle())
            it.setOnClickListener {
                activity?.let{activity ->
                    projectID?.let{projectID ->
                        roomID?.let{roomID ->
                            GlobalScope.launch(Dispatchers.Main){
                                APIRoom.requestModels(projectID, roomID, requestSKP, requestDXF).onSuccess {
                                    showNotifcationMessage(R.string.requested_models_sent, activity)
                                }.onError {
                                    showNotifcationMessage(R.string.requested_models_error, activity)
                                }
                            }
                        }
                    }
                }
            }
        }

        view.findViewById<CheckBox>(R.id.checkBoxSKP).apply{
            isEnabled = hasSKP
        }.setOnCheckedChangeListener { buttonView, isChecked ->
            requestSKP =  isChecked
            toggleButton(evaluateForToggle())
        }

        view.findViewById<CheckBox>(R.id.checkBoxDXF).apply{
            isEnabled = hasDXF
        }.setOnCheckedChangeListener { buttonView, isChecked ->
            requestSKP =  isChecked
            toggleButton(evaluateForToggle())
        }

    }


    private fun evaluateForToggle(): Boolean{
        return requestDXF || requestSKP
    }

    private fun toggleButton(enable: Boolean){
               requestButton?.run{
                   isEnabled = enable
                   //setBackgroundColor(if(!enable) Color.parseColor("#22FFFFFF") else
                   //    resources.getColor(R.color.paarl) )
               }
    }

    private fun showNotifcationMessage(@StringRes message: Int, activity: FragmentActivity){
        activity?.let{activity ->
            NotificationModalFragment(message, R.string.ok){ }.run {
                show(
                    activity.supportFragmentManager,
                    "NotificationFragment"
                )
            }
        }
    }

}