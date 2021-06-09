package com.studiocinqo.diardeonandroid.connect

import android.app.Activity
import android.content.Context
import android.util.Log
import com.studiocinqo.diardeonandroid.connect.container.IResult
import com.studiocinqo.diardeonandroid.connect.container.JWrap
import org.json.JSONObject


class APIAuthentication {

    class ICredentials(val email: String, val password: String) {}

    companion object {

        suspend fun autoLogin(activity: Activity): IResult<JSONObject> {
            Log.d("APIAuthentication", "autoLogin")
            return getCredentials(activity)?.let {
                login(it.email, it.password)
            } ?: IResult.NOT_AUTHENTICATED

        }

        suspend fun login(email: String, password: String): IResult<JSONObject> {
            Log.d("APIAuthentication", "login")
            val jsonObject = JSONObject()
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            return APICore.instance.post("/_api/login", jsonObject)
        }

        suspend fun signup(
            email: String, password: String,
            confirmPassword: String, language: String
        ): IResult<ISuccess> {
            return APICore.instance.post("/_api/user/signup",
                JSONObject()?.also {
                    it.put("email", email)
                    it.put("password", password)
                    it.put("confirmpassword", confirmPassword)
                }).map{ obj -> ISuccess.getSuccess(obj)}
        }

        suspend fun passwordresetrequest(email: String): IResult<ISuccess> {
            return APICore.instance.post("/_api/user/requestpasswordreset",
                JSONObject()?.also {
                    it.put("email", email)
                }).map{ obj -> ISuccess.getSuccess(obj)}
        }

        suspend fun passwordreset(token: String, password: String, confirm: String): Any? {
            return APICore.instance.post("/_api/user/resetpassword",
                JSONObject()?.also {
                    it.put("token", token)
                    it.put("password", password)
                    it.put("confirm", confirm)
                });
        }

        suspend fun verifyemail(token: String): Any? {
            return APICore.instance.post("/_api/user/verifyemail", JSONObject()?.also {
                it.put("token", token)
            })
        }

        suspend fun logout(): Any? {
            return APICore.instance.getAsJSON("/_api/logout")
        }

        suspend fun isTokenValid(token: String): Any? {
            return APICore.instance.getAsJSON("/_api/user/istokenvalid/${token}")
        }

        suspend fun getLogin(): IResult<JSONObject> {
            return APICore.instance.getAsJSON("/_api/getLogin")
        }

        private fun getCredentials(activity: Activity): ICredentials? {
            return activity.getPreferences(Context.MODE_PRIVATE).getString("email", null)
                ?.let { email ->
                    activity.getPreferences(Context.MODE_PRIVATE).getString("password", null)
                        ?.let { password ->
                            return ICredentials(email, password)
                        }
                }
        }

        fun addCredentials(email: String, password: String, activity: Activity) {
            activity.getPreferences(Context.MODE_PRIVATE)?.let { sharedPref ->
                with(sharedPref.edit()) {
                    putString("email", email)
                    putString("password", password)
                    apply()
                }
            }
        }

        fun clearCredentials(activity: Activity){
            activity.getPreferences(Context.MODE_PRIVATE)?.let { sharedPref ->
                sharedPref.edit().clear()
            }
        }
    }


}

class ISuccess(val success: Boolean, val error: String?){

    companion object{
        fun getSuccess(obj: JSONObject): ISuccess? {
            return JWrap(obj)?.run{
                getBoolean("success")?.let{
                    ISuccess(it, getString("error"))
                }
            }
        }
    }

}





