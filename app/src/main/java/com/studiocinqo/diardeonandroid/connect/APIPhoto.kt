package com.studiocinqo.diardeonandroid.connect

import android.graphics.Bitmap
import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger

class APIPhoto {

    companion object {

        val Queue = Queue().apply {
            val mutex = Mutex()
            GlobalScope.launch(Dispatchers.Main) {
                while (true) {
                    receive().run {
                        mutex.withLock {
                            //delay(20*1000)
                            addImages(projectID, roomID, stream).onSuccess {
                                decrement()
                                update.offer(true)
                            }.onError {
                                decrement()
                                update.offer(true)
                            }
                        }
                    }
                }
            }
        }

        suspend fun getImage(imageURL: String): IResult<Bitmap> {
            Log.d("APIPhoto", "getImage")
            return APICore.instance.getAsBitmap(
                "/_uploads/${imageURL}"
            )
        }

        suspend fun addImages(
            projectId: String,
            roomId: String,
            stream: InputStream
        ): IResult<Any> {
            Log.d("APIPhoto", "addImage")
            return APICore.instance.postImage(
                "/_api/projects/${projectId}/rooms/${roomId}/images",
                stream
            )
        }

        suspend fun deleteImage(projectId: String, roomId: String, imageId: String): IResult<Any> {
            return APICore.instance.delete("/_api/projects/${projectId}/rooms/${roomId}/images/${imageId}")
        }

    }

}

class Queue {

    val update = Channel<Boolean>()
    val queue = Channel<FileObject>(10)
    private val pendingCount = AtomicInteger(0)

    suspend fun receive(): FileObject {
        return queue.receive().apply {
            update.offer(true)
        }
    }

    fun offer(value: FileObject) {
        Log.d("APIPhoto", "Add to queue")
        queue.offer(value)
        pendingCount.incrementAndGet()
        update.offer(true)
    }

    fun getQueueCount(): Int {
        return pendingCount.get()
    }

    fun increment() {
        pendingCount.incrementAndGet()
    }

    fun decrement() {
        pendingCount.decrementAndGet()
    }

    suspend fun waitForUpdate() {
        update.receive()
    }

}

class FileObject(
    val projectID: String, val roomID: String,
    val stream: InputStream
) {}