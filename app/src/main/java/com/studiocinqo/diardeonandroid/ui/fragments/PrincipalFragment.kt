package com.studiocinqo.diardeonandroid.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PrincipalFragment : DiardeBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        GlobalScope.launch(Dispatchers.Main) {
            activity?.let { activity ->
                APIAuthentication.autoLogin(activity).onError {
                    handleError(it, activity)
                }.onSuccess {
                }
            }

        }

        (activity as AppCompatActivity)?.supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_principal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonNewProject).setOnClickListener {
            findNavController().navigate(R.id.action_PrincipalFragment_to_newProjectFragment)
        }

        view.findViewById<Button>(R.id.buttonBrowse).setOnClickListener {
            findNavController().navigate(R.id.action_PrincipalFragment_to_projectsFragment)
        }

        view.findViewById<Button>(R.id.buttonSettings).setOnClickListener {
            findNavController().navigate(R.id.action_PrincipalFragment_to_instructionsFragment)
        }

    }


}