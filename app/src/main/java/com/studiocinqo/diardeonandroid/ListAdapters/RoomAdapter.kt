package com.studiocinqo.diardeonandroid.ListAdapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.container.IRoom
import com.studiocinqo.diardeonandroid.connect.container.Status
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import com.studiocinqo.diardeonandroid.ui.fragments.room.RoomsFragment

class RoomAdapter(private val roomFragment: RoomsFragment) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>()  {

    private var isEditMode: Boolean = false
    private val items: MutableList<Item> = mutableListOf()

    inner class Item(var checked: Boolean, val room: IRoom) {}


    class RoomViewHolder(val view: View) :
        RecyclerView.ViewHolder(view){

        val checkBox: CheckBox = view.findViewById(R.id.roomCheckBox)
        val textView: TextView = view.findViewById(R.id.roomText)
        var id: String? = null

        fun setChecked(bool: Boolean) {
            checkBox.isChecked = bool
        }

        fun setCheckboxVisible(bool: Boolean) {
            if (bool) {
                this.checkBox.visibility = View.VISIBLE
            } else {
                this.checkBox.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.roomitem, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.room.name
        holder.id = item.room.id
        holder.setCheckboxVisible(isEditMode)
        holder.setChecked(item.checked)

        holder.view.setOnClickListener {view ->
            if (!isEditMode) {
                when(item.room.processing?.status){
                    Status.OPEN -> R.id.action_roomsFragment_to_photosFragment
                    Status.PENDING -> R.id.action_roomsFragment_to_pendingRequestFragment
                    Status.PROCESSED -> R.id.action_roomsFragment_to_resultFragment
                    else -> R.id.action_roomsFragment_to_photosFragment
                }.let{
                    view?.findNavController()?.navigate(it, Bundle().also {
                            item.room.id?.let { id ->
                                it.putString(ARG_PROJECTID, roomFragment.projectID)
                                it.putString(ARG_ROOMID, id)}
                        }
                    )
                }
            }
        }

        holder.view.setOnLongClickListener {
            if (!isEditMode) {
                isEditMode = !isEditMode
                item.checked = !item.checked
                notifyDataSetChanged()
                roomFragment.adviseActionBar(items.filter { it.checked }.map { it.room.id })
            }
            true
        }

        holder.checkBox.setOnClickListener {
            item.checked = !item.checked
            holder.setChecked(item.checked)
            roomFragment.adviseActionBar(items.filter { it.checked }.map { it.room.id })
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    public fun addItem(value: IRoom){
        items.add(Item(false, value))
    }

    public fun setItems(value: List<IRoom>){
        items.clear();
        value.forEach { element -> items.add(Item(false, element)) }
    }

    fun revokeEditMode() {
        this.isEditMode = false
        items.forEach { item ->
            item.checked = false
        }
        notifyDataSetChanged()
    }

}