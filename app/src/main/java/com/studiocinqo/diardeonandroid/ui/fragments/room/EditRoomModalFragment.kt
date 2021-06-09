package com.studiocinqo.diardeonandroid.ui.fragments.room

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.container.IRoom


class EditRoomModalFragment(
    private val room: IRoom,
    private val callbackConfirm: ((id: String, name: String, description: String) -> Unit)?
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DiardeDialogTheme)

            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.fragment_edit_room_modal, null)?.apply {
                findViewById<EditText>(R.id.editTextRoomNameForModal)?.apply {
                    setText(room.name)
                }
                findViewById<EditText>(R.id.editTextRoomDescriptionFroModal)?.apply {
                    setText(room.description)
                }
            })
                .setPositiveButton(R.string.submit,
                    DialogInterface.OnClickListener { dialogInterface, id ->
                        callbackConfirm?.let { cb ->
                            dialog?.findViewById<EditText>(R.id.editTextRoomNameForModal)?.text?.let { name ->
                                dialog?.findViewById<EditText>(R.id.editTextRoomDescriptionFroModal)?.text?.let { description ->
                                    if (name.isNotEmpty()) {
                                        cb(room.id, name.toString(), description.toString())
                                    }
                                }
                            }
                        }
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialogInterfce, id ->
                        getDialog()?.cancel()
                    })
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}