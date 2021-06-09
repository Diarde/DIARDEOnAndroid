package com.studiocinqo.diardeonandroid.ListAdapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.container.IPhoto
import com.studiocinqo.diardeonandroid.ui.fragments.result.photos.PhotoBaseFragment

class PhotoAdapter(private val photoFragment: PhotoBaseFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isEditMode: Boolean = false
    private val items: MutableList<Item> = mutableListOf()
    public var dummyCount = 0

    inner class Item(
        var checked: Boolean,
        val photo: IPhoto,
        val image: Bitmap
    ) {}

    class PhotoViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        val checkBox: CheckBox
        val imageView: ImageView
        var id: String? = null

        init {
            checkBox = view.findViewById(R.id.photoCheckBox)
            imageView = view.findViewById(R.id.imageView)
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

    class DummyViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        val cardView: CardView

        init {
            cardView = view.findViewById(R.id.cardView)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photoitem, parent, false)
                return PhotoViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dummyitem, parent, false)
                return DummyViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.dummyitem, parent, false)
                return DummyViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position < items.size) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == 0) {
            val holder: PhotoViewHolder = holder as PhotoViewHolder
            items[position]?.let { item ->
                holder.id = item.photo.id
                holder.setCheckboxVisible(isEditMode)
                holder.setChecked(item.checked)
                holder.imageView.setImageBitmap(item.image)

                holder.view.setOnLongClickListener {
                    if (photoFragment.editable && !isEditMode) {
                        isEditMode = !isEditMode
                        item.checked = !item.checked
                        notifyDataSetChanged()
                        photoFragment.adviseActionBar(items.filter { it.checked }
                            .mapNotNull { it.photo.id })
                    }
                    true
                }

                holder.checkBox.setOnClickListener {
                    item.checked = !item.checked
                    holder.setChecked(item.checked)
                    photoFragment.adviseActionBar(items.filter { it.checked }
                        .mapNotNull { it.photo.id })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size + dummyCount
    }

    fun addItem(value: IPhoto, image: Bitmap) {
        items.add(Item(false, value, image))
    }

    fun removeItem(id: String) {
        items.firstOrNull { it?.photo?.id == id }?.run {
            items.remove(this)
        }
    }

    fun hasItem(id: String): Boolean {
        return items.firstOrNull { it?.photo?.id == id }?.let { true } ?: false
    }

    fun clear() {
        items.clear()
    }

    fun revokeEditMode() {
        this.isEditMode = false
        items.forEach { item ->
            item?.checked = false
        }
        notifyDataSetChanged()
    }

}