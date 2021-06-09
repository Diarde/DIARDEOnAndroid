package com.studiocinqo.diardeonandroid.ui.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupFragment : DiardeBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.findViewById<Button>(R.id.buttonSignUpSubmit)?.setOnClickListener {
            view?.findViewById<TextView>(R.id.signupEmail)?.text?.let { email ->
                view?.findViewById<TextView>(R.id.signupPassword)?.text?.let { password ->
                    view?.findViewById<TextView>(R.id.signupPasswordRepeat)?.text?.let { passwordRepeat ->
                        GlobalScope.launch(Dispatchers.Main) {
                            APIAuthentication.signup(
                                email.toString(), password.toString(),
                                passwordRepeat.toString(), "en"
                            ).onSuccess { success ->
                                if(success.success){
                                    login(email.toString(), password.toString())
                                }else{
                                    displayErrorMessage(success.error)
                                }
                            }.onError { activity?.let{activity -> handleError(it, activity)} }
                        }
                    }
                }
            }
        }
        view?.findViewById<Button>(R.id.buttonSignupCancel)?.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun displayErrorMessage(message: String?){
        view?.findViewById<TextView>(R.id.signupErrorMessage)?.run {
            message?.let{ message ->
                visibility = View.VISIBLE
                text = message
            } ?: run { visibility = View.INVISIBLE }
            true
        }
    }

    private fun login(email: String, password: String){
        GlobalScope.launch(Dispatchers.Main) {
            activity?.let{ activity ->
                APIAuthentication.login(email, password).onSuccess {
                    findNavController().navigate(R.id.action_signupFragment_to_PrincipalFragment)
                }.onError {
                    handleError(it, activity)
                }
            }
        }
    }

}