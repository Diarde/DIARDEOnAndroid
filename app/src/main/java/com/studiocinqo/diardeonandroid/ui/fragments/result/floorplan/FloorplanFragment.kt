package com.studiocinqo.diardeonandroid.ui.fragments.result.floorplan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.container.IModel
import com.studiocinqo.diardeonandroid.enginecanvas.floorplanbuilder.Floorplanbuilder
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.result.ARG_MODEL
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import com.studiocinqo.diardeonandroid.ui.views.FloorplanView
import org.json.JSONObject

class FloorplanFragment : Fragment() {
    private var projectID: String? = null
    private var roomID: String? = null
    private var floorplan: IModel.IData.IFloorplan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
            floorplan = it.getString(ARG_MODEL)?.let{ floorplan ->
                    IModel.IData.IFloorplan.getFloorplan(JSONObject(floorplan))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_floorplan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity)?.run {
            supportActionBar?.run {
                setCustomView(R.layout.toolbar_default)
                findViewById<TextView>(R.id.actionbarDefaultTitle)?.run {
                    text = getString(R.string.roomplan)
                }
                show()
            }
        }

        view.findViewById<FloorplanView>(R.id.floorplanView)?.let{floorplanview ->
            val engine = floorplanview.engine

            floorplan?.let{floorplan ->
                Floorplanbuilder(floorplan, engine)
                floorplanview.invalidate()
            }
        }
    }

}