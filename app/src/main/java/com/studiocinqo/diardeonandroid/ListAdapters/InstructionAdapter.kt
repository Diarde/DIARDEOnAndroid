package com.studiocinqo.diardeonandroid.ListAdapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.ui.fragments.instructions.InstructionsFragment

class InstructionAdapter(private val tutorialFragment: InstructionsFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<Either<String, Bitmap>> = mutableListOf()

    sealed abstract class Either<A, B>(val left: A?, val right: B?) {
        class Left<A, B>(left: A) : Either<A, B>(left, null){
            override fun isLeft(): Boolean {return true}
        }
        class Right<A, B>(right: B) : Either<A, B>(null, right){
            override fun isLeft(): Boolean {return false}
        }
        abstract fun isLeft(): Boolean
    }

    class TextViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }

        var text: String? = ""
         set(value){ textView.text = value}
    }

    class ImageViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {

        val imageView: ImageView

        init {
            imageView = view.findViewById(R.id.imageView)
        }

        var image: Bitmap? = null
            set(value){ imageView.setImageBitmap(value)}

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
                    .inflate(R.layout.textitem, parent, false)
                return TextViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.imageitem, parent, false)
                return ImageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.imageitem, parent, false)
                return DummyViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(items[position].let{item -> item.isLeft()}) 0 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            items[position]?.let { item ->
                if(item.isLeft()){
                    (holder as TextViewHolder).text = item.left
                } else {
                    (holder as ImageViewHolder).image = item.right
                }
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addText(text: String) {
        items.add(Either.Left(text))
    }

    fun addImage(image: Bitmap) {
        items.add(Either.Right(image))
    }

}