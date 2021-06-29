package es.uam.eps.tfg.menuPlanner.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.database.Gender
import es.uam.eps.tfg.menuPlanner.database.LifeStyle
import es.uam.eps.tfg.menuPlanner.database.User
import kotlinx.android.synthetic.main.fragment_init_user_info.*
import kotlinx.android.synthetic.main.user_info.*

class InitUserInfoFragment : Fragment() {

    private var username: String = ""
    private var age: Int = 0
    private var weight: Int = 0
    private var height: Int = 0
    private var gender: Gender = Gender.NONE
    private var lifeStyle: LifeStyle = LifeStyle.NONE
    private var userID: String = ""
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        preferences = activity.getSharedPreferences("pref", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(
            this,
            LoginViewModel.FACTORY((requireContext().applicationContext as MenuApplication).repository)
        )
            .get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_init_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                init_info_username.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                username = s.toString()
            }
        }
        val ageWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                init_info_age.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                age = s.toString().toIntOrNull() ?: 0
            }
        }
        val weightWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                init_info_weight.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                weight = s.toString().toIntOrNull() ?: 0
            }
        }
        val heightWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                init_info_height.error = null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                height = s.toString().toIntOrNull() ?: 0
            }
        }

        init_info_height_editText.addTextChangedListener(heightWatcher)
        init_info_weight_editText.addTextChangedListener(weightWatcher)
        init_info_username_editText.addTextChangedListener(usernameWatcher)
        init_info_age_editText.addTextChangedListener(ageWatcher)

        male_checkbox.setOnClickListener {
            if (it is RadioButton)
                if (it.isChecked){
                    gender = Gender.MAN
                    female_checkbox.isChecked = false
                }
                else
                    gender = Gender.NONE
        }

        female_checkbox.setOnClickListener {
            if (it is RadioButton)
                if (it.isChecked){
                    gender = Gender.WOMAN
                    male_checkbox.isChecked = false
                }
                else
                    gender = Gender.NONE
        }

        sedentary_checkbox.setOnClickListener {
            if (it is RadioButton)
                if (it.isChecked){
                    lifeStyle = LifeStyle.SEDENTARY
                    m_active_checkbox.isChecked = false
                    active_checkbox.isChecked = false
                }
                else
                    lifeStyle = LifeStyle.NONE
        }

        m_active_checkbox.setOnClickListener {
            if (it is RadioButton)
                if (it.isChecked){
                    lifeStyle = LifeStyle.M_ACTIVE
                    sedentary_checkbox.isChecked = false
                    active_checkbox.isChecked = false
                }
                else
                    lifeStyle = LifeStyle.NONE
        }

        active_checkbox.setOnClickListener {
            if (it is RadioButton)
                if (it.isChecked){
                    lifeStyle = LifeStyle.ACTIVE
                    sedentary_checkbox.isChecked = false
                    m_active_checkbox.isChecked = false
                }
                else
                    lifeStyle = LifeStyle.NONE
        }

        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                userID = user.id_user
                username = user.username
                init_info_username_editText.setText(user.username)
                age = user.age
                init_info_age_editText.setText(user.age.toString())
                height = user.height
                init_info_height_editText.setText(user.height.toString())
                weight = user.weight
                init_info_weight_editText.setText(user.weight.toString())
                gender = user.gender
                if (user.gender.equals(Gender.MAN))
                    male_checkbox.isChecked = true
                else
                    female_checkbox.isChecked = true
                lifeStyle = user.lifeStyle
                when (user.lifeStyle){
                    LifeStyle.SEDENTARY -> sedentary_checkbox.isChecked = true
                    LifeStyle.M_ACTIVE -> m_active_checkbox.isChecked = true
                    else -> active_checkbox.isChecked = true
                }
            }
        })

        button_next.setOnClickListener {

            if (username.isBlank())
                init_info_username.error = getString(R.string.field_empty_error)
            else if (age == 0)
                init_info_age.error = getString(R.string.field_empty_error)
            else if (height == 0)
                init_info_height.error = getString(R.string.field_empty_error)
            else if (weight == 0)
                init_info_weight.error = getString(R.string.field_empty_error)
            else if (gender.equals(Gender.NONE))
                Snackbar.make(view, getString(R.string.empty_selection, "gender"), Snackbar.LENGTH_SHORT).show()
            else if (lifeStyle.equals(LifeStyle.NONE))
                Snackbar.make(view, getString(R.string.empty_selection, "physical condition"), Snackbar.LENGTH_SHORT).show()
            else {
                viewModel.setUser(
                    User(
                        userID,
                        username,
                        age,
                        height,
                        weight,
                        gender,
                        lifeStyle
                    )
                )

                findNavController().navigate(R.id.action_initUserInfoFragment_to_mainMenuFragment)
            }
        }
    }
}