package com.studiocinqo.diardeonandroid.ui.fragments.result.photos

import android.os.Bundle
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
import com.studiocinqo.diardeonandroid.connect.APIRoom
import com.studiocinqo.diardeonandroid.ListAdapters.PhotoAdapter
import com.studiocinqo.diardeonandroid.connect.APIPhoto
import com.studiocinqo.diardeonandroid.connect.container.Status
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.ConfirmationModalFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.NotificationModalFragment
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import kotlinx.coroutines.*
import java.util.*

class TakePhotosFragment : PhotoBaseFragment() {
    private var projectID: String? = null
    private var roomID: String? = null
    private var photoAdapter: PhotoAdapter? = null
    private var isRunning = true
    private var created = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
            this.photoAdapter = PhotoAdapter(this)
            waitForUpdate()
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
                    text = getString(R.string.photos)
                }
                show()
            }
        }
        return inflater.inflate(R.layout.fragment_take_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.photosView)
        recyclerView.adapter = photoAdapter

        view.findViewById<FloatingActionButton>(R.id.takeNewPhoto).setOnClickListener {
            findNavController().navigate(
                R.id.action_photosFragment_to_cameraKitFragment,
                Bundle().apply {
                    putString(ARG_PROJECTID, projectID)
                    putString(ARG_ROOMID, roomID)
                })
        }

        view.findViewById<FloatingActionButton>(R.id.submitPhotos).setOnClickListener {
            (ConfirmationModalFragment(R.string.submit_photos_message,
                R.string.cancel,
                R.string.submit,
                { id -> Unit },
                { id ->
                    NotificationModalFragment(R.string.photos_submitted_message, R.string.ok) {
                        projectID?.let { projectID ->
                            roomID?.let { roomID ->
                                GlobalScope.launch(Dispatchers.Main) {
                                    APIRoom.submitPhotos(projectID, roomID).onSuccess { room ->
                                        room.processing?.let { processing ->
                                            if (processing.status == Status.PENDING) {
                                                findNavController().navigate(R.id.action_photosFragment_to_pendingRequestFragment,
                                                    Bundle().apply {
                                                        putString(ARG_ROOMID, roomID)
                                                        putString(ARG_PROJECTID, projectID)
                                                    })
                                            }
                                        }
                                    }.onError {
                                        activity?.let { activity ->
                                            handleError(it, activity)
                                        }
                                    }
                                }
                            }
                        }
                    }.let { fragment ->
                        activity?.run {
                            fragment.show(
                                supportFragmentManager,
                                "DialogFragment"
                            )
                        }
                    }
                }
            )).let { fragment ->
                activity?.run {
                    fragment.show(
                        supportFragmentManager,
                        "DialogFragment"
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        photoAdapter?.run { clear() }
        loadAndUpdatePhotoList(APIPhoto.Queue.getQueueCount())
        isRunning = true
    }

    override fun onPause() {
        super.onPause()
        created = false
        isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun adviseActionBar(ids: List<String>) {
        val count = ids.count()
        (activity as MainActivity)?.let { activity ->

            activity.supportActionBar?.let { supportActionBar ->
                supportActionBar.setCustomView(R.layout.toolbar_edititem)
                setPhotoCancelLister(activity, supportActionBar)
                setPhotoDeleteListener(activity, ids)
                activity.findViewById<ImageView>(R.id.iconItemEditEdit)
                    ?.run { visibility = View.GONE }
            }
        }

    }

    private fun setPhotoCancelLister(
        activity: MainActivity,
        supportActionBar: ActionBar
    ) {
        activity.findViewById<ImageView>(R.id.iconItemEditCancel)?.run {

            this.setOnClickListener {
                photoAdapter?.revokeEditMode()
                supportActionBar.setCustomView(R.layout.toolbar_default)
            }
        }
    }

    private fun setPhotoDeleteListener(
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
                            deletePhotosRecursively(ids)
                            {
                                loadAndUpdatePhotoList(APIPhoto.Queue.getQueueCount())
                                activity.supportActionBar?.let { supportActionBar ->
                                    photoAdapter?.revokeEditMode()
                                    supportActionBar.setCustomView(R.layout.toolbar_default)
                                }
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

    private fun waitForUpdate(showEmptyListNotification: Boolean = false): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            APIPhoto.Queue.run {
                while (true) {
                    waitForUpdate().run {
                        if (isRunning) loadAndUpdatePhotoList(getQueueCount())
                    }
                }
            }
        }
    }

    private fun loadAndUpdatePhotoList(count: Int) {
        this.photoAdapter?.let { photoAdapter ->
            GlobalScope.launch(Dispatchers.Main) {
                projectID?.let { projectID ->
                    roomID?.let { roomID ->
                        APIRoom.getRoom(projectID, roomID).onSuccess { room ->
                            GlobalScope.launch(Dispatchers.Main) {
                                if (created && room.photos.isEmpty()) activity?.let { activity ->
                                    showEmptyListNotification(
                                        activity
                                    )
                                }
                                room.photos.forEach { photo ->
                                    if (!photoAdapter.hasItem(photo.id)) {
                                        val filename = photo.filename + ".thump.JPG"
                                        APIPhoto.getImage(filename).onSuccess { image ->
                                            photoAdapter.addItem(photo, image)
                                            photoAdapter.notifyDataSetChanged()
                                        }.onError {
                                            activity?.let { activity ->
                                                handleError(it, activity)
                                            }
                                        }
                                    }
                                }
                                photoAdapter.dummyCount = count
                                photoAdapter.notifyDataSetChanged()
                            }
                        }.onError { activity?.let { activity -> handleError(it, activity) } }
                    }
                }
            }
        }
    }

    private fun deletePhotosRecursively(ids: List<String>, cb: () -> Unit) {
        fun recursive(ids: LinkedList<String>) {
            ids.poll()?.let { id ->
                GlobalScope.launch(Dispatchers.Main) {
                    projectID?.let { projectID ->
                        roomID?.let { roomID ->
                            APIPhoto.deleteImage(projectID, roomID, id).onSuccess {
                                photoAdapter?.removeItem(id)
                                photoAdapter?.notifyDataSetChanged()
                                recursive(ids)
                            }.onError {
                                recursive(ids)
                            }
                        }
                    }
                }
                true
            } ?: cb()
        }
        recursive(LinkedList(ids))
    }

    private fun showEmptyListNotification(activity: FragmentActivity) {
        NotificationModalFragment(
            R.string.empty_photo_list_notfication,
            R.string.ok
        ) { }.run {
            show(
                activity.supportFragmentManager,
                "NotificationFragment"
            )
        }
    }

    override val editable: Boolean
        get() = true

}