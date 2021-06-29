package es.uam.eps.tfg.menuPlanner.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.R
import kotlinx.android.synthetic.main.fragment_choose_login.*

class ChooseLoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleAuth: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var viewModel: LoginViewModel
    private var RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireNotNull(this.activity)
        viewModel = ViewModelProvider(
            this,
            LoginViewModel.FACTORY((requireContext().applicationContext as MenuApplication).repository)
        )
            .get(LoginViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        googleAuth = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity as Activity, googleAuth)
        activity.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            firebaseAuthWithGoogle(task)
        }
    }

    private fun firebaseAuthWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if (account != null){
                Log.d("Login", "google log in succeed" + account.id)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener(activity as Activity){ task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "signInWithCredential:success")
                        viewModel.setUser()
                    } else {
                        Log.w("Login", "signInWithCredential:failure", task.exception)
                    }
                }
            }

        }catch (e: ApiException){
            Log.d("Login", "signInResult:failed code=" + e.getStatusCode())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_in_with_email.setOnClickListener {
            findNavController().navigate(R.id.action_chooseLoginFragment_to_loginFragment)
        }

        button_google_sign_in.setOnClickListener {
            GoogleSignIn.getLastSignedInAccount(activity as Activity)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
}