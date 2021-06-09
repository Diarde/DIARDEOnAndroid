package com.studiocinqo.diardeonandroid.ui.fragments.auxiliary

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.studiocinqo.diardeonandroid.R

class ConfirmationModalFragment(
    @StringRes private val messageText: Int,
    @StringRes private val cancelText: Int,
    @StringRes private val confirmText: Int,
    private val callbackCancel: ((id: Int) -> Unit)?,
    private val callbackConfirm: ((id: Int) -> Unit)?
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DiardeDialogTheme)

            val inflater = requireActivity().layoutInflater;
            builder.setView(inflater.inflate(R.layout.fragment_confirmation_modal, null)?.apply {
                findViewById<TextView>(R.id.confirmationModalText)?.apply {
                    setText(messageText)
                }

            })
            builder.run {
                callbackConfirm?.let { callback ->
                    setPositiveButton(
                        confirmText
                    ) { dialog, id ->
                        callback(id)
                    }
                }
                callbackCancel?.let { callback ->
                    setNegativeButton(
                        cancelText
                    ) { dialog, id ->
                        callback(id)
                    }
                }
            }
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }


}