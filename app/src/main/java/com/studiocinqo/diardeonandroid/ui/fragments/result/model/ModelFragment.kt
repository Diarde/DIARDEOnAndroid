package com.studiocinqo.diardeonandroid.ui.fragments.result.model

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.ui.fragments.result.ARG_MODEL
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import com.studiocinqo.diardeonandroid.ui.views.ModelView
import org.json.JSONObject

class ModelFragment : Fragment() {
    private var projectID: String? = null
    private var roomID: String? = null
    private var model: IModel.IData.IModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
            model = it.getString(ARG_MODEL)?.let{ model -> IModel.IData.IModel.getModel(JSONObject(model))}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)?.run {
            supportActionBar?.run {
                setCustomView(R.layout.toolbar_default)
                findViewById<TextView>(R.id.actionbarDefaultTitle)?.run {
                    text = getString(R.string.model)
                }
                show()
            }
        }

       view.findViewById<ModelView>(R.id.modelView)?.run {
           model?.let{ model ->
               setModel(model)
           }
        }
    }

    companion object {

        fun newInstance(projectID: String, roomID: String) =
            ModelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PROJECTID, projectID)
                    putString(ARG_ROOMID, roomID)
                }
            }

    }
}