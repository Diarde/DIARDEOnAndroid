package com.studiocinqo.diardeonandroid.ui.fragments.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIProject
import com.studiocinqo.diardeonandroid.ListAdapters.ProjectAdapter
import com.studiocinqo.diardeonandroid.connect.utility.Status
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.ConfirmationModalFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.NotificationModalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ProjectsFragment : DiardeBaseFragment() {

    private val projectAdapter = ProjectAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadAndUpdateProjectsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)?.run {
            supportActionBar?.run {
                setCustomView(R.layout.toolbar_default)
                findViewById<TextView>(R.id.actionbarDefaultTitle)?.run {
                    text = getString(R.string.projects)
                }
                show()
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.projectsView)
        recyclerView.adapter = projectAdapter
        view?.findViewById<TextView>(R.id.actionbarDefaultTitle)?.let { it.text = "Projects" }
        view.findViewById<FloatingActionButton>(R.id.fabCreateNewProject).setOnClickListener {
            (activity as MainActivity)?.let { activity ->
                NewProjectModalFragment { name, description ->
                    GlobalScope.launch(Dispatchers.Main) {
                        APIProject.postProject(name, description).onSuccess {
                            loadAndUpdateProjectsList()
                        }.onError { handleError(it, activity) }
                    }
                }.show(
                    activity.supportFragmentManager,
                    "DialogFragment"
                )
            }
        }
    }

    fun adviseActionBar(ids: List<String>) {
        val count = ids.count()
        (activity as MainActivity)?.let { activity ->
            activity.supportActionBar?.let { supportActionBar ->
                supportActionBar.setCustomView(R.layout.toolbar_edititem)
                setProjectCancelLister(activity, supportActionBar)
                setProjectEditListener(activity, ids.firstOrNull()?.let {
                    if (count == 1) {
                        it
                    } else {
                        null
                    }
                })
                setProjectDeleteListener(activity, ids)
            }
        }

    }

    private fun setProjectCancelLister(
        activity: MainActivity,
        supportActionBar: ActionBar
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditCancel)?.run {

            this.setOnClickListener {
                projectAdapter.revokeEditMode()
                supportActionBar.setCustomView(R.layout.toolbar_default)
            }
        }
    }

    private fun setProjectEditListener(
        activity: MainActivity,
        id: String?
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditEdit)?.run {
            if (id != null) {
                this.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        APIProject.getProject(id).onSuccess {
                            EditProjectModalFragment(it) { id, name, description ->
                                GlobalScope.launch(Dispatchers.Main) {
                                    APIProject.updateProject(id, name, description).onSuccess {
                                        loadAndUpdateProjectsList()
                                    }.onError {
                                        handleError(it, activity)
                                    }
                                }
                            }.show(
                                activity.supportFragmentManager,
                                "DialogFragment"
                            )
                        }
                    }

                }
                this.visibility = View.VISIBLE
            } else {
                this.visibility = View.GONE
            }
        }
    }

    private fun setProjectDeleteListener(
        activity: MainActivity,
        ids: List<String>
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditDelete)?.run {
            if (ids.isNotEmpty()) {
                this.setOnClickListener {
                    (ConfirmationModalFragment(
                        R.string.modal_delete_projects,
                        R.string.cancel,
                        R.string.delete,
                        { id -> Unit },
                        { id ->
                            deleteProjectsRecursively(ids)
                            { loadAndUpdateProjectsList() }
                        }
                    )).run {
                        show(
                            activity.supportFragmentManager,
                            "DialogFragment"
                        )
                    }
                }
                this.visibility = View.VISIBLE
            } else {
                this.visibility = View.GONE
            }
        }
    }

    private fun loadAndUpdateProjectsList() {
        activity?.let { activity ->
            GlobalScope.launch(Dispatchers.Main) {
                APIProject.getProjects().onSuccess { array ->
                    if (array.size === 0) showEmptyListNotification(activity)
                    projectAdapter.setItems(array.sortedBy { it.name })
                    projectAdapter.notifyDataSetChanged()
                }.onError {
                    handleError(it, activity)
                }
            }
        }
    }

    private fun deleteProjectsRecursively(ids: List<String>, cb: () -> Unit) {
        fun recursive(ids: LinkedList<String>) {
            ids.poll()?.let { id ->
                GlobalScope.launch(Dispatchers.Main) {
                    APIProject.deleteProject(id).onSuccess {
                        recursive(ids)
                    }.onError {
                        recursive(ids)
                    }
                }
                true
            } ?: cb()
        }
        recursive(LinkedList(ids))
    }

    private fun showEmptyListNotification(activity: FragmentActivity){
        NotificationModalFragment(
            R.string.empty_project_list_notfication,
            R.string.ok
        ) { }.run {
                show(
                    activity.supportFragmentManager,
                    "NotificationFragment"
                )
            }
        }

}


