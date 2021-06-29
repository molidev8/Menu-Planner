package es.uam.eps.tfg.menuPlanner.userProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import es.uam.eps.tfg.menuPlanner.database.*
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory
import kotlinx.coroutines.*

class ProfileViewModel(private val repository: MenuRepository) : ViewModel() {

    companion object {
        val FACTORY = viewModelFactory(::ProfileViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + viewModelJob)


    var savedRecipes: LiveData<List<Recipe>> = liveData {
        val data = repository.getSavedRecipes()
        emit(data)
    }

    var user: LiveData<User?> = liveData {
        val data = repository.getUser(true)
        emit(data)
    }

    var shoppingList: LiveData<List<ShoppingList>> = liveData {
        val data = repository.getShoppingList()
        emit(data)
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        uiScope.launch {
            repository.updateShoppingList(shoppingList)
        }
    }

    fun setUser(user: User) {
        uiScope.launch {
            repository.setUser(user)

        }
    }
}