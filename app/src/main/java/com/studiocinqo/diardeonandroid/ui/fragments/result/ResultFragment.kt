package com.studiocinqo.diardeonandroid.ui.fragments.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIGeometry
import com.studiocinqo.diardeonandroid.connect.IModelWrapper
import com.studiocinqo.diardeonandroid.ui.MainActivity
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

const val ARG_MODEL = "model"
const val ARG_HASDXF = "hasDXF"
const val ARG_HASSKP = "hasSKP"

class ResultFragment : DiardeBaseFragment() {
    private var projectID: String? = null
    private var roomID: String? = null
    private var model: IModelWrapper? = null
    private val isFileDonwloadEnabled: Channel<Boolean> = Channel()
    private val isModelViewEnabled: Channel<Boolean> = Channel()
    private val isPlanViewEnabled: Channel<Boolean> = Channel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
        }

        GlobalScope.launch(Dispatchers.Main) {
            projectID?.let{projectID ->
                roomID?.let{roomID ->
                    APIGeometry.loadModel(projectID, roomID).onSuccess { _model ->
                         model = _model
                        isFileDonwloadEnabled.offer(_model.dxf || _model.skp)
                        isModelViewEnabled.offer(true)
                        isPlanViewEnabled.offer(true)
                    }.onError { activity?.let{ activity -> handleError(it, activity)} }
                }
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
                    text = getString(R.string.results)
                }
                show()
            }
        }
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonViewPhotos).setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_viewPhotosFragment,
                Bundle().also {
                    it.putString(ARG_PROJECTID, projectID)
                    it.putString(ARG_ROOMID, roomID)
                })
        }

        view.findViewById<Button>(R.id.buttonDownload).apply{
            GlobalScope.launch(Dispatchers.Main) {
                isEnabled = isFileDonwloadEnabled.receive()
            }
        }.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_requestFilesFragment,
                Bundle().also {
                    it.putString(ARG_PROJECTID, projectID)
                    it.putString(ARG_ROOMID, roomID)
                    it.putBoolean(ARG_HASDXF, model?.dxf ?: false)
                    it.putBoolean(ARG_HASSKP, model?.skp ?: false)
                })
        }

        view.findViewById<Button>(R.id.buttonViewPlan).apply{
            GlobalScope.launch(Dispatchers.Main) {
                isEnabled = isPlanViewEnabled.receive()
            }
        }.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_floorplanFragment,
                Bundle().also {
                    it.putString(ARG_PROJECTID, projectID)
                    it.putString(ARG_ROOMID, roomID)
                    it.putString(ARG_MODEL, model?.model?.data?.floorplan?.toString())
                })
        }

        view.findViewById<Button>(R.id.buttonViewModel).apply{
            GlobalScope.launch(Dispatchers.Main) {
                isEnabled =isModelViewEnabled.receive()
            }
        }.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_modelFragment,
                Bundle().also {
                    it.putString(ARG_PROJECTID, projectID)
                    it.putString(ARG_ROOMID, roomID)
                    it.putString(ARG_MODEL, model?.model?.data?.model?.toString())
                })
        }

    }


    override fun onResume() {
        super.onResume()
        view?.findViewById<Button>(R.id.buttonDownload)?.apply{
           model?.let{model ->
               isEnabled = model.skp || model.dxf
           }
        }
        view?.findViewById<Button>(R.id.buttonViewModel)?.apply{
            model?.let{model ->
                isEnabled = true
            }
        }
        view?.findViewById<Button>(R.id.buttonViewPlan)?.apply{
            model?.let{model ->
                isEnabled = true
            }
        }
    }

}