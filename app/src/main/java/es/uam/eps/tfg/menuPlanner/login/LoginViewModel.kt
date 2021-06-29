package es.uam.eps.tfg.menuPlanner.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.database.Rating
import es.uam.eps.tfg.menuPlanner.database.User
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class LoginViewModel(private val repository: MenuRepository) : ViewModel() {

    companion object {
        val FACTORY = viewModelFactory(::LoginViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean>
        get() = _loginResult

    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String>
        get() = _signUpResult

    private var rating: Rating? = null

    init {
        uiScope.launch {
            try {
                rating = repository.getRating()
            } catch (e: Exception) {
                Log.d("Login", "User not logged yet")
            }
        }
    }

    val user: LiveData<User?> = liveData {
        val data = repository.getUser(false)
        emit(data)
    }

    fun login(email: String, password: String) {
        uiScope.launch {
            val state = repository.signInWithEmail(email, password)
            _loginResult.value = state
        }
    }

    fun signUp(email: String, password: String) {
        uiScope.launch {
            try {
                repository.signUpWithEmail(email, password)
                _signUpResult.value = ""
            } catch (e: FirebaseAuthUserCollisionException) {
                _signUpResult.value = e.message
            } catch(e: FirebaseAuthWeakPasswordException) {
                _signUpResult.value = e.message
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _signUpResult.value = e.message
            }
        }
    }

    fun setUser(user: User) {
        uiScope.launch {
            withContext(IO) {
                repository.setUser(user)
                repository.setRating(rating ?: Rating())
            }
        }
    }

    fun setUser() {
        uiScope.launch {
            withContext(IO) {
                val user = repository.getUser(false)
                if (user != null)
                    repository.setUser(user)
            }
        }
    }
}