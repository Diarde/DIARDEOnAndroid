package com.studiocinqo.diardeonandroid.ui.fragments.result.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIRoom
import com.studiocinqo.diardeonandroid.ListAdapters.PhotoAdapter
import com.studiocinqo.diardeonandroid.connect.APIPhoto
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewPhotosFragment : PhotoBaseFragment() {
    private var projectID: String? = null
    private var roomID: String? = null
    private var photoAdapter: PhotoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
            this.photoAdapter = PhotoAdapter(this)
            loadAndUpdatePhotoList()
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
        return inflater.inflate(R.layout.fragment_view_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.photosView)
        recyclerView.adapter = photoAdapter
    }

    private fun loadAndUpdatePhotoList(){
        this.photoAdapter?.let { photoAdapter ->
            GlobalScope.launch(Dispatchers.Main) {
                projectID?.let { projectID ->
                    roomID?.let { roomID ->
                        APIRoom.getRoom(projectID, roomID).onSuccess { room ->
                            GlobalScope.launch(Dispatchers.Main) {
                                room.photos.forEach { photo ->
                                    APIPhoto.getImage(photo.filename + ".thump.JPG").onSuccess { image ->
                                        photoAdapter.addItem(photo, image)
                                        photoAdapter.notifyDataSetChanged()
                                    }.onError { activity?.let{ activity -> handleError(it, activity)} }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun adviseActionBar(photoIDs: List<String>) {
    }

    override val editable: Boolean
        get() = false

}