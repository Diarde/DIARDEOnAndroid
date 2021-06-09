package com.studiocinqo.diardeonandroid.ui.fragments.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIProject
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewProjectFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_new_project, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonCancelNewProject).setOnClickListener {
            findNavController().navigate(R.id.fromNewProjectToPrincipal)
        }

        view.findViewById<Button>(R.id.buttonCreateNewProject).setOnClickListener {
            view.findViewById<EditText>(R.id.newTextProjectName).text?.let { name ->
                view.findViewById<EditText>(R.id.newTextProjectDescription).text?.let { description ->
                    GlobalScope.launch(Dispatchers.Main) {
                        APIProject.postProject(name.toString(), description.toString()).onSuccess {
                            findNavController().navigate(R.id.action_newProjectFragment_to_roomsFragment,
                                Bundle().apply{ putString(ARG_PROJECTID, it)})
                        }
                    }
                }
            }
        }
    }

}

