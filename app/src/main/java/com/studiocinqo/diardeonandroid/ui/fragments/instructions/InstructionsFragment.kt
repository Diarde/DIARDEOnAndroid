package com.studiocinqo.diardeonandroid.ui.fragments.instructions

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.studiocinqo.diardeonandroid.ListAdapters.InstructionAdapter
import com.studiocinqo.diardeonandroid.ListAdapters.PhotoAdapter
import com.studiocinqo.diardeonandroid.R

class InstructionsFragment : Fragment() {

    private var adapter: InstructionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.adapter = InstructionAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.instructionsView)
        recyclerView.adapter = adapter

        this.adapter?.run{

            addText(getString(R.string.getting_started_1))
            addText(getString(R.string.getting_started_2))
            addImage(BitmapFactory.decodeStream(context?.assets?.open("images/tutorial/NewProject.jpg")))
            addText(getString(R.string.getting_started_3))
            addImage(BitmapFactory.decodeStream(context?.assets?.open("images/tutorial/NewRoom.png")))
            addText(getString(R.string.getting_started_4))
            addText(getString(R.string.getting_started_2))
            addText(getString(R.string.getting_started_2))
            addText(getString(R.string.getting_started_2))
        }

    }


}