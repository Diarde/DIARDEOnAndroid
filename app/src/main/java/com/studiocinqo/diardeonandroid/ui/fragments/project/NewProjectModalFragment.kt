package com.studiocinqo.diardeonandroid.ui.fragments.project

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.studiocinqo.diardeonandroid.R

class NewProjectModalFragment(
    private val callbackSubmit: ((name: String, description: String) -> Unit)?) : DialogFragment() {

    var builder: AlertDialog.Builder? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            builder = AlertDialog.Builder(it, R.style.DiardeDialogTheme)
            
            val inflater = requireActivity().layoutInflater;

            builder?.setView(inflater.inflate(R.layout.fragment_new_project_modal, null))
            builder?.setPositiveButton(R.string.submit,
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog?.findViewById<EditText>(R.id.newTextProjectNameForModal)?.text?.let{name ->
                        dialog?.findViewById<EditText>(R.id.newTextProjectDescriptionFroModal)?.text?.let{description ->
                            callbackSubmit?.let{cb ->
                                cb(name.toString(), description.toString())}
                        }
                    }
                })?.setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialogInterface, id ->
                        dialog?.cancel()
                    })
            builder?.create()



        } ?: throw IllegalStateException("Activity cannot be null")
    }



}