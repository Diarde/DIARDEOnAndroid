package com.studiocinqo.diardeonandroid.ui.fragments.auxiliary

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.studiocinqo.diardeonandroid.R


class NotificationModalFragment(
    @StringRes private val messageText: Int,
    @StringRes private val confirmText: Int,
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
                }
                builder.create()

            } ?: throw IllegalStateException("Activity cannot be null")
        }


    }