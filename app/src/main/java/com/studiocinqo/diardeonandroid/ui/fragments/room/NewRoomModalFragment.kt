package com.studiocinqo.diardeonandroid.ui.fragments.room

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.studiocinqo.diardeonandroid.R

class NewRoomModalFragment(
    private val callbackSubmit: ((name: String, description: String) -> Unit)?
) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it, R.style.DiardeDialogTheme)

                val inflater = requireActivity().layoutInflater;

                builder.setView(inflater.inflate(R.layout.fragment_new_room_modal, null))
                    // Add action buttons
                    .setPositiveButton(R.string.submit,
                        DialogInterface.OnClickListener { dialogInterface, id ->
                            dialog?.findViewById<EditText>(R.id.newTextRoomNameForModal)?.text?.let{ name ->
                                dialog?.findViewById<EditText>(R.id.newTextRoomNameForModal)?.text?.let{ description ->
                                    callbackSubmit?.let{cb ->
                                        cb(name.toString(), description.toString())}
                                }
                            }
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            getDialog()?.cancel()
                        })
                builder.create()

            } ?: throw IllegalStateException("Activity cannot be null")
        }

    }