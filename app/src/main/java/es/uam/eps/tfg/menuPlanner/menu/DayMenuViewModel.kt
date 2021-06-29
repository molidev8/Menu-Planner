package es.uam.eps.tfg.menuPlanner.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.database.Recipe
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory2

class DayMenuViewModel(private val repository: MenuRepository, day: String): ViewModel() {

    companion object {
        val FACTORY = viewModelFactory2(::DayMenuViewModel)
    }

    val recipes: LiveData<List<Recipe>> = liveData {
        val data = repository.getRecipesFromDay("%$day%")
        val list = mutableListOf<Recipe>()
        list.add(data.find { it -> it.dishTypes.contains("breakfast") }!!)
        list.add(data.find { it -> it.dishTypes.contains("lunch") }!!)
        list.add(data.find { it -> it.dishTypes.contains("dinner") }!!)
        emit(list.toList())
    }
}