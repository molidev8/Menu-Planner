package es.uam.eps.tfg.menuPlanner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavGraph


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ActivityViewModel
    private lateinit var navController: NavController
    private lateinit var graph: NavGraph
    private lateinit var preferences: SharedPreferences

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this, Observer { authState ->
                when(authState){
                    ActivityViewModel.AuthenticationState.AUTHENTICATED -> {
                        if (preferences.getBoolean("isSaved", false)) {
                            graph.startDestination = R.id.mainMenuFragment
                            navController.graph = graph
                            Log.d("Login", "setting main menu")
                        } else {
                            graph.startDestination = R.id.initUserInfoFragment
                            navController.graph = graph
                            Log.d("Login", "setting init")
                        }
                    }
                    else -> {
                        graph.startDestination = R.id.chooseLoginFragment
                        navController.graph = graph
                        Log.d("Login", "setting choose login")
                    }
                }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        navController = findNavController(R.id.myNavHostFragment)
        graph = navController.navInflater.inflate(R.navigation.navigation)
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)

        viewModel = ViewModelProvider(
            this,
            ActivityViewModel.FACTORY((applicationContext as MenuApplication).repository)
        )
            .get(ActivityViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        observeAuthenticationState()
    }



}
