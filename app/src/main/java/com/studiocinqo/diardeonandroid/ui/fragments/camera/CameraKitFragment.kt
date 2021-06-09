package com.studiocinqo.diardeonandroid.ui.fragments.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaActionSound
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.camerakit.CameraKitView
import com.jpegkit.Jpeg
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIPhoto
import com.studiocinqo.diardeonandroid.connect.FileObject
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_PROJECTID
import com.studiocinqo.diardeonandroid.ui.fragments.room.ARG_ROOMID

class CameraKitFragment : Fragment(), SensorEventListener {
    private var projectID: String? = null
    private var roomID: String? = null
    private var mAccelerometer: Sensor? = null
    private var mMagnetometer: Sensor? = null
    private var _rotation = 0

    private var cameraKitView: CameraKitView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            projectID = it.getString(ARG_PROJECTID)
            roomID = it.getString(ARG_ROOMID)
        }
        (activity as AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         context?.run{
             (getSystemService(Context.SENSOR_SERVICE) as SensorManager).let{ mSensorManager ->
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            } 
        }
        return inflater.inflate(R.layout.fragment_camera_kit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraKitView = view.findViewById(R.id.camera);
        view.findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {

            //activity?.let{ activity ->
                //val audio = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val mSound = MediaActionSound()
                mSound.play(MediaActionSound.SHUTTER_CLICK)
            //}

            cameraKitView?.captureImage { cameraKitView, bytes ->

                        Jpeg(bytes).let{ jpeg ->
                            activity?.run{
                                    jpeg.rotate((-_rotation + 360) % 360)
                            }
                            jpeg.jpegBytes.let{ bytes ->
                                view.findViewById<ImageView>(R.id.imageView)?.run{
                                    setImageBitmap(
                                    BitmapFactory.decodeByteArray(
                                        bytes, 0, bytes.size))
                                    visibility = View.VISIBLE
                                }
                                projectID?.let{ projectID ->
                                    roomID?.let{ roomID ->
                                        FileObject(projectID, roomID, bytes.inputStream()).let{ file ->
                                            APIPhoto.Queue.offer(file)
                                        }
                                    }
                                }
                            }
                    }
                }
            }
    }

     override fun onStart() {
        super.onStart()
        cameraKitView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView?.onResume()
        context?.let{ context ->
            (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).let{ mSensorManager ->
                mAccelerometer?.let{mAccelerometer ->
                    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                }
                mMagnetometer?.let{ mMagnetometer ->
                    mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL)
                }
            }
        }

    }

    override fun onPause() {
        cameraKitView?.onPause()
        context?.let { context ->
            (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).let { mSensorManager ->
                mSensorManager.unregisterListener(this)
            }
        }
        super.onPause()
    }

    override fun onStop() {
        cameraKitView?.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults!!)
        cameraKitView?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {

        fun newInstance() =
            CameraKitFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let{event ->
            if (event.sensor == mAccelerometer) {
                if(Math.abs(event.values[1]) > Math.abs(event.values[0])) {
                    if (event.values[1] > 1) {
                        _rotation = 0
                    } else if (event.values[1] < -1) {
                        _rotation = 180
                    }
                }else{
                    if (event.values[0] > 1) {
                        _rotation = 90
                    } else if (event.values[0] < -1) {
                        _rotation = 270
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}