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
import com.studiocinqo.diardeonandroid.connect.container.IProject
import com.studiocinqo.diardeonandroid.ui.fragments.project.ProjectsFragment
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID

class ProjectAdapter(private val projectFragment: ProjectsFragment) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var isEditMode: Boolean = false
    private val items: MutableList<Item> = mutableListOf()

    inner class Item(var checked: Boolean, val project: IProject) {}

    class ProjectViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        val checkBox: CheckBox
        val textView: TextView
        var id: String? = null

        init {
            textView = view.findViewById(R.id.projectText)
            checkBox = view.findViewById(R.id.projectCheckBox)
        }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.projectitem, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.project.name
        holder.id = item.project.id
        holder.setCheckboxVisible(isEditMode)
        holder.setChecked(item.checked)

        holder.view.setOnClickListener {
            if (!isEditMode) {

                it?.findNavController()?.navigate(
                    R.id.fromProjectsToRooms, Bundle().also {
                        item.project.id?.let { id -> it.putString(ARG_PROJECTID, id) }
                    }
                )
            }
        }

        holder.view.setOnLongClickListener {
            if (!isEditMode) {
                isEditMode = !isEditMode
                item.checked = !item.checked
                notifyDataSetChanged()
                projectFragment.adviseActionBar(items.filter { it.checked }.map { it.project.id })
            }
            true
        }

        holder.checkBox.setOnClickListener {
            item.checked = !item.checked
            holder.setChecked(item.checked)
            projectFragment.adviseActionBar(items.filter { it.checked }.map { it.project.id })
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    public fun addItem(value: IProject) {
        items.add(Item(false, value))
    }

    public fun setItems(value: List<IProject>) {
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