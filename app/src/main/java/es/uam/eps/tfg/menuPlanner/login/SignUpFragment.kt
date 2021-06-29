package es.uam.eps.tfg.menuPlanner.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

    private var email: String = ""
    private var password: String = ""
    private var password2: String = ""
    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            LoginViewModel.FACTORY((requireContext().applicationContext as MenuApplication).repository)
        )
            .get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                login_email_text.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email = s.toString()
            }
        }

        val passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                login_password_text.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password = s.toString()
            }
        }

        val password2Watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                login_password_text2.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                password2 = s.toString()
            }
        }

        login_email_text_editText.addTextChangedListener(emailWatcher)
        login_password_text_editText.addTextChangedListener(passwordWatcher)
        login_password_text2_editText.addTextChangedListener(password2Watcher)

        viewModel.signUpResult.observe(viewLifecycleOwner, Observer { value ->
            if(value.isNotEmpty())
                Snackbar.make(view, value, Snackbar.LENGTH_SHORT).show()
            else {
                viewModel.login(email, password)
                findNavController().navigate(R.id.action_signUpFragment_to_initUserInfoFragment)
            }
        })

        button_next_sign_up.setOnClickListener {

            if (email.isBlank())
                login_email_text.error = getString(R.string.field_empty_error)
            else if (password.isBlank())
                login_password_text.error = getString(R.string.field_empty_error)
            else if (password2.isBlank())
                login_password_text2.error = getString(R.string.field_empty_error)
            else if(password != password2)
                Snackbar.make(view, getString(R.string.unmatched_password), Snackbar.LENGTH_SHORT).show()
            else  {
                viewModel.signUp(email, password)
            }
        }

        button_cancel.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

}