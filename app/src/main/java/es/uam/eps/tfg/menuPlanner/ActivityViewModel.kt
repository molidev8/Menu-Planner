package es.uam.eps.tfg.menuPlanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory
import es.uam.eps.tfg.menuPlanner.database.*
import kotlinx.coroutines.*

class ActivityViewModel(private val repository: MenuRepository): ViewModel() {

    companion object {
        val FACTORY = viewModelFactory(::ActivityViewModel)
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val checkScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val authenticationState = repository.authState.map { user ->
        if (user != null)
            AuthenticationState.AUTHENTICATED
        else
            AuthenticationState.UNAUTHENTICATED
    }
}