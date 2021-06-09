package com.studiocinqo.diardeonandroid.ui.fragments.room

import android.os.Bundle
import android.util.Log
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
import com.studiocinqo.diardeonandroid.connect.APIRoom
import com.studiocinqo.diardeonandroid.ListAdapters.RoomAdapter
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.ConfirmationModalFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.NotificationModalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

public const val ARG_PROJECTID = "projectID"
public const val ARG_ROOMID = "roomID"

class RoomsFragment : DiardeBaseFragment() {
    public var projectID: String? = null
    private var roomAdapter: RoomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            this.roomAdapter = RoomAdapter(this)?.also { roomAdapter ->
                loadAndUpdateRoomList()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.run {
            supportActionBar?.run {
                setCustomView(R.layout.toolbar_default)
                findViewById<TextView>(R.id.actionbarDefaultTitle)?.run {
                    text = getString(R.string.rooms)
                }
                show()
            }
        }
        return inflater.inflate(R.layout.fragment_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.roomsView)
        recyclerView.adapter = roomAdapter

        view.findViewById<FloatingActionButton>(R.id.createNewRoom).setOnClickListener {
            (activity as MainActivity)?.let { activity ->
                NewRoomModalFragment { name, description ->
                    GlobalScope.launch(Dispatchers.Main) {
                        projectID?.let { projectID ->
                            APIRoom.postRoom(projectID, name, description).onSuccess {
                                /*GlobalScope.launch(Dispatchers.Main) {
                                    projectID?.let { projectID ->
                                        APIRoom.getRooms(projectID).onSuccess { array ->
                                            roomAdapter?.setItems(array.sortedBy { it.name })
                                            roomAdapter?.notifyDataSetChanged()
                                        }
                                    }
                                }*/
                                findNavController().navigate(R.id.action_roomsFragment_to_photosFragment,
                                    Bundle().apply{
                                        putString(ARG_PROJECTID, projectID)
                                        putString(ARG_ROOMID, it)})
                            }.onError {
                                handleError(it, activity)
                            }
                        }
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
                setRoomCancelLister(activity, supportActionBar)
                setRoomEditListener(activity, ids.firstOrNull()?.let {
                    if (count == 1) {
                        it
                    } else {
                        null
                    }
                })
                setRoomDeleteListener(activity, ids)
            }
        }

    }

    private fun setRoomCancelLister(
        activity: MainActivity,
        supportActionBar: ActionBar
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditCancel)?.run {

            this.setOnClickListener {
                roomAdapter?.revokeEditMode()
                supportActionBar.setCustomView(R.layout.toolbar_default)
            }
        }
    }

    private fun setRoomEditListener(
        activity: MainActivity,
        id: String?
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditEdit)?.run {
            if (id != null) {
                this.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        projectID?.let { projectID ->
                            APIRoom.getRoom(projectID, id).onSuccess {
                                EditRoomModalFragment(it) { id, name, description ->
                                    GlobalScope.launch(Dispatchers.Main) {
                                        APIRoom.updateRoom(projectID, id, name, description).onSuccess {
                                            loadAndUpdateRoomList()
                                        }
                                    }
                                }.show(
                                    activity.supportFragmentManager,
                                    "DialogFragment"
                                )
                            }
                        }
                    }
                }
                this.visibility = View.VISIBLE
            } else {
                this.visibility = View.GONE
            }
        }
    }

    private fun setRoomDeleteListener(
        activity: MainActivity,
        ids: List<String>
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditDelete)?.run {
            if (ids.isNotEmpty()) {
                this.setOnClickListener {
                    (ConfirmationModalFragment(
                        R.string.modal_delete_rooms,
                        R.string.cancel,
                        R.string.delete,
                        { id -> Unit },
                        { id ->
                            Unit
                            deleteRoomsRecursively(ids) {
                                loadAndUpdateRoomList()
                            }
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

    private fun loadAndUpdateRoomList() {
        activity?.let{activity ->
            GlobalScope.launch(Dispatchers.Main) {
                projectID?.let { projectID ->
                    APIProject.getProject(projectID).onSuccess { project ->
                        project.rooms?.let{rooms ->
                            if (rooms.size === 0) showEmptyListNotification(activity)
                            roomAdapter?.setItems(rooms.sortedBy { it.name })
                            roomAdapter?.notifyDataSetChanged()
                        }
                    }.onError {
                        handleError(it, activity)
                    }
                }
            }
        }
    }

    private fun deleteRoomsRecursively(ids: List<String>, cb: () -> Unit) {
        projectID?.let { projectID ->
            fun recursive(ids: LinkedList<String>) {
                ids.poll()?.let { id ->
                    GlobalScope.launch(Dispatchers.Main) {
                        APIRoom.deleteRoom(projectID, id).onSuccess {
                            recursive(ids)
                        }.onError {
                            recursive(ids)
                        }
                    }
                    true
                } ?: cb()
            }
            recursive(LinkedList(ids))
            true
        } ?: cb()
    }

    private fun showEmptyListNotification(activity: FragmentActivity){
        NotificationModalFragment(
            R.string.empty_room_list_notfication,
            R.string.ok
        ) { }.run {
            show(
                activity.supportFragmentManager,
                "NotificationFragment"
            )
        }
    }

}