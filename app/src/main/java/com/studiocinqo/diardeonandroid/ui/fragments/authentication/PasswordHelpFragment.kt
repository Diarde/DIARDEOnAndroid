package com.studiocinqo.diardeonandroid.ui.fragments.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.studiocinqo.diardeonandroid.R
import com.studiocinqo.diardeonandroid.connect.APIAuthentication
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.DiardeBaseFragment
import com.studiocinqo.diardeonandroid.ui.fragments.auxiliary.NotificationModalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PasswordHelpFragment : DiardeBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.buttonSubmitRecovery).setOnClickListener {
            view.findViewById<TextView>(R.id.recoveryEmail)?.let { textView ->
                textView.text.toString().let { email ->
                    if (email.isBlank()) {
                        textView.hint = getString(R.string.enter_valid_email)
                    } else
                        GlobalScope.launch(Dispatchers.Main) {
                            APIAuthentication.passwordresetrequest(email).onSuccess { success ->
                                if (success.success) {
                                    activity?.run {
                                        NotificationModalFragment(R.string.reset_email_issued, R.string.ok) {
                                            findNavController().navigate(R.id.action_passwordHelpFragment_to_loginFragment)
                                        }.run {
                                            show(supportFragmentManager, "PasswordHelp")
                                        }
                                    }
                                } else {
                                    displayErrorMessage(success.error)
                                }
                            }.onError {
                                activity?.let { activity ->
                                    handleError(it, activity)
                                }
                            }
                        }
                }
            }
        }

        view.findViewById<Button>(R.id.buttonCancelRecovery).setOnClickListener {
            findNavController().navigate(R.id.action_passwordHelpFragment_to_loginFragment)
        }

    }

    private fun displayErrorMessage(message: String?){
        view?.findViewById<TextView>(R.id.passwordHelpErrorMessage)?.run{
            message?.let{ message ->
                visibility = View.VISIBLE
                text = message
            } ?: run { visibility = View.INVISIBLE }
            true
        }
    }


}