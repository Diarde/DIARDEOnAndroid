package com.studiocinqo.diardeonandroid.connect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IResult
import com.studiocinqo.diardeonandroid.connect.utility.Status
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.*
import javax.net.ssl.*
import javax.security.cert.CertificateException

class APICore {

    private val msCookieManager = CookieManager()
    val baseURL = "https://mobile.diarde.com" //"http://172.20.10.2:3000" //

    private constructor(){

    }

    companion object{

        val instance = APICore()

    }

    init {

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, java.security.SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        val allHostsValid = HostnameVerifier { hostname, session -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)

    }


    suspend fun getAsJSON(url: String): IResult<JSONObject> {
            var result: IResult<JSONObject> = IResult.INTERNAL_SERVER_ERROR
            GlobalScope.async {
                withContext(Dispatchers.IO) {
                    with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                        try {
                            requestMethod = "GET"
                            addRequestProperty("Content-Type", "application/json")
                            getCookiesFromStore()?.run {
                                addRequestProperty("Cookie", this)
                            }

                            connect()

                            BufferedReader(InputStreamReader(inputStream)).use {
                                val response = StringBuffer()

                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    response.append(inputLine)
                                    inputLine = it.readLine()
                                }

                                result = IResult(responseCode, JSONObject(response.toString()))
                            }

                        } catch (E: UnknownHostException){
                            result = IResult( Status.NOCONNECTION.code, null)
                        } catch(E: ConnectException){
                            result = IResult(Status.NOCONNECTION.code, null)
                        } catch (e: Exception) {
                            result = IResult(responseCode, null)
                        }
                    }
                }
            }.await()
            return result
    }

    suspend fun getAsJArray(url: String): IResult<JSONArray> {
        var result: IResult<JSONArray> = IResult.INTERNAL_SERVER_ERROR
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                    try {
                        requestMethod = "GET"
                        addRequestProperty("Content-Type", "application/json")
                        getCookiesFromStore()?.run {
                            addRequestProperty("Cookie", this)
                        }

                        connect()

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }

                            result = IResult(responseCode, JSONArray(response.toString()))
                        }

                    } catch (E: UnknownHostException){
                        result = IResult( Status.NOCONNECTION.code, null)
                    } catch(E: ConnectException){
                        result = IResult(Status.NOCONNECTION.code, null)
                    } catch (e: Exception) {
                        result = IResult(responseCode, null)
                    }
                }
            }
        }.await()
        return result
    }

    suspend fun getAsBitmap(url: String): IResult<Bitmap> {
        Log.d("bitman", "get bitmap")
        var result: IResult<Bitmap> = IResult.INTERNAL_SERVER_ERROR
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                    try {
                        requestMethod = "GET"
                        getCookiesFromStore()?.run {
                            addRequestProperty("Cookie", this)
                        }
                        setDoInput(true)
                        connect()

                        val input: InputStream = getInputStream()
                        result =  IResult(responseCode, BitmapFactory.decodeStream(input))

                    } catch (E: UnknownHostException){
                        result = IResult( Status.NOCONNECTION.code, null)
                    } catch(E: ConnectException){
                        result = IResult(Status.NOCONNECTION.code, null)
                    } catch (e: Exception) {
                        result = IResult(responseCode, null)
                    }
                }
            }
        }.await()
        return result
    }

    suspend fun put(url: String, data: JSONObject): IResult<JSONObject>{
        var result: IResult<JSONObject> = IResult.INTERNAL_SERVER_ERROR
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                    try {
                        requestMethod = "PUT"
                        addRequestProperty("Content-Type", "application/json")

                        getCookiesFromStore()?.run {
                            addRequestProperty("Cookie", this)
                        }

                        connect()

                        val wr = OutputStreamWriter(getOutputStream());
                        wr.write(data.toString());
                        wr.flush();

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }

                            result = IResult(responseCode,JSONObject(response.toString()))

                        }

                    } catch (E: UnknownHostException){
                        result = IResult( Status.NOCONNECTION.code, null)
                    } catch(E: ConnectException){
                        result = IResult(Status.NOCONNECTION.code, null)
                    } catch (E: Exception) {
                        result = IResult(responseCode, null)
                    }
                }
            }
        }.await()
        return result
    }

    suspend fun delete(url: String): IResult<JSONObject> {
        var result: IResult<JSONObject> = IResult.INTERNAL_SERVER_ERROR
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                    try {
                        requestMethod = "DELETE"

                        getCookiesFromStore()?.run {
                            addRequestProperty("Cookie", this)
                        }

                        connect()

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }

                            result = IResult(responseCode,JSONObject(response.toString()))

                        }

                    } catch (E: UnknownHostException){
                        result = IResult( Status.NOCONNECTION.code, null)
                    } catch(E: ConnectException){
                        result = IResult(Status.NOCONNECTION.code, null)
                    } catch (E: Exception) {
                        result = IResult(responseCode, null)
                    }
                }
            }
        }.await()
        return result
    }

    suspend fun postImage(url: String, _inputStream: InputStream): IResult<JSONObject> {
        var result: IResult<JSONObject> = IResult.INTERNAL_SERVER_ERROR
        GlobalScope.async {
            withContext(Dispatchers.IO) {
                with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                    try {
                        requestMethod = "POST"

                        getCookiesFromStore()?.run {
                            addRequestProperty("Cookie", this)
                        }

                        Multipart(this).run{
                              addFilePart("file", _inputStream, "image.jpg", "image/jpg")
                             finish()
                        }

                        BufferedReader(InputStreamReader(inputStream)).use {
                            val response = StringBuffer()

                            var inputLine = it.readLine()
                            while (inputLine != null) {
                                response.append(inputLine)
                                inputLine = it.readLine()
                            }

                            result = IResult(responseCode, JSONObject(response.toString()))

                        }

                    } catch (E: UnknownHostException){
                        result = IResult( Status.NOCONNECTION.code, null)
                    } catch(E: ConnectException){
                        result = IResult(Status.NOCONNECTION.code, null)
                    } catch (E: Exception) {
                        result = IResult(responseCode, null)
                    }
                }
            }
        }.await()
        return result
    }

    suspend fun post(url: String, data: JSONObject): IResult<JSONObject> {
            var result: IResult<JSONObject> = IResult.INTERNAL_SERVER_ERROR
            GlobalScope.async {
                withContext(Dispatchers.IO) {
                    with(URL(getFullURL(url)).openConnection() as HttpURLConnection) {
                        try {
                            requestMethod = "POST"
                            addRequestProperty("Content-Type", "application/json")

                            getCookiesFromStore()?.run {
                                addRequestProperty("Cookie", this)
                            }

                            connect()

                            val wr = OutputStreamWriter(getOutputStream());
                            wr.write(data.toString());
                            wr.flush();

                            extractCookies(headerFields)

                            BufferedReader(InputStreamReader(inputStream)).use {
                                val response = StringBuffer()

                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    response.append(inputLine)
                                    inputLine = it.readLine()
                                }

                                result = IResult(responseCode, JSONObject(response.toString()))

                            }

                        } catch (E: UnknownHostException) {
                            result = IResult(Status.NOCONNECTION.code, null)
                        } catch(E: ConnectException){
                            result = IResult(Status.NOCONNECTION.code, null)
                        } catch (E: Exception) {
                            result = IResult(responseCode, null)
                        }
                    }
                }
            }.await()
            return result
    }

    private fun extractCookies(headerFields: Map<String, List<String>>) {
        headerFields["Set-Cookie"]?.run {
            this.forEach {
                msCookieManager.cookieStore.add(
                    null,
                    HttpCookie.parse(it)[0]
                );
            }
        }
    }

    private fun getCookiesFromStore(): String? {
        if (msCookieManager.cookieStore.cookies.size > 0) {
            return TextUtils.join(";", msCookieManager.cookieStore.cookies);
        }
        return null
    }

    private fun getFullURL(url: String): String {
        return "${this.baseURL}${url}"
    }

}