package com.studiocinqo.diardeonandroid.connect

import java.io.*
import java.net.HttpURLConnection
import java.net.URL



class Multipart
@Throws(IOException::class)
constructor(private val connection: HttpURLConnection) {

    companion object {
        private val LINE_FEED = "\r\n"
        private val maxBufferSize = 1024 * 1024
        private val charset = "UTF-8"
    }

    // creates a unique boundary based on time stamp
    private val boundary: String = "----" + System.currentTimeMillis()
    private val _outputStream: OutputStream
    private val writer: PrintWriter

    init {

        connection.run{
            setRequestProperty("Accept-Charset", "UTF-8")
            setRequestProperty("Connection", "Keep-Alive")
            setRequestProperty("Cache-Control", "no-cache")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary)
            setChunkedStreamingMode(maxBufferSize)
            doInput = true
            doOutput = true    // indicates POST method
            useCaches = false
            _outputStream = outputStream
            writer = PrintWriter(OutputStreamWriter(outputStream, charset), true)
        }
    }

    fun addFormField(name: String, value: String) {
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
            .append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.append(value).append(LINE_FEED)
        writer.flush()
    }

    @Throws(IOException::class)
    fun addFilePart(fieldName: String, inputStream: InputStream, fileName: String, fileType: String) {
        writer.append("--").append(boundary).append(LINE_FEED)
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
            .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED)
        writer.append("Content-Type: ").append(fileType).append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()

        inputStream.copyTo(_outputStream, maxBufferSize)

        _outputStream.flush()
        inputStream.close()
        writer.append(LINE_FEED)
        writer.flush()
    }

    fun addHeaderField(name: String, value: String) {
        writer.append(name + ": " + value).append(LINE_FEED)
        writer.flush()
    }

    @Throws(IOException::class)
    fun finish() {
        writer.append(LINE_FEED).flush()
        writer.append("--").append(boundary).append("--")
            .append(LINE_FEED)
        writer.close()


    }


}