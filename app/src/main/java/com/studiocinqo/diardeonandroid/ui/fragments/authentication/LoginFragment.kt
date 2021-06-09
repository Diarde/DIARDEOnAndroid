package com.studiocinqo.diardeonandroid.ui.fragments.authentication

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.connect.container.JWrap
import com.studiocinqo.diardeonandroid.connect.utility.Status
import com.studiocinqo.diardeonandroid.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity)?.run {
            supportActionBar?.run {
                hide()
            }
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.forgotPassword).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordHelpFragment)
        }

        view.findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            view.findViewById<TextView>(R.id.loginEmail).text?.let { email ->
                view.findViewById<TextView>(R.id.loginPassword).text?.let { password ->
                    view.findViewById<TextView>(R.id.loginErrorMessage).visibility = View.INVISIBLE
                    GlobalScope.launch(Dispatchers.Main) {
                        APIAuthentication.login(email.toString(), password.toString()).onSuccess {
                            JWrap(it)?.let { json ->
                                json.getString("authentication")?.let { authentication ->
                                    if (authentication == "success") {
                                        activity?.let { activity ->
                                            APIAuthentication.addCredentials(
                                                email.toString(),
                                                password.toString(),
                                                activity
                                            )
                                        }
                                        findNavController().navigate(R.id.PrincipalFragment)
                                    } else {
                                        json.getString("error")?.let { error ->
                                            view.findViewById<TextView>(R.id.loginErrorMessage)
                                                .apply {
                                                    text = error
                                                    visibility = View.VISIBLE
                                                }
                                        }
                                    }
                                }
                            }
                        }.onError { code ->
                            if(code === Status.NOCONNECTION.code){
                                view.findViewById<TextView>(R.id.loginErrorMessage)
                                    .apply {
                                        text = getString(R.string.no_connection)
                                        visibility = View.VISIBLE
                                    }
                            }
                        }
                    }
                }
            }
        }

        view.findViewById<Button>(R.id.buttonSignUp).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }


}