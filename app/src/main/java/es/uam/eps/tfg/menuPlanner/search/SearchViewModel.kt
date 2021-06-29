package es.uam.eps.tfg.menuPlanner.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.database.Recipe
import es.uam.eps.tfg.menuPlanner.database.Repository
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: MenuRepository) : ViewModel() {

    companion object {
        val FACTORY = viewModelFactory(::SearchViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>>
            get() = _searchResults

    fun searchRecipes(query: String) {
        uiScope.launch {
            _searchResults.value = (repository as Repository).searchRecipes(query)
        }
    }

}