package es.uam.eps.tfg.menuPlanner.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import es.uam.eps.tfg.menuPlanner.database.*
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: MenuRepository, id: Int): ViewModel() {

    companion object {
        val FACTORY = viewModelFactory2(::RecipeViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe>
        get() = _recipe

    init {
        uiScope.launch {
            _recipe.value = repository.getRecipe(id)
        }
    }

    val stats: LiveData<Pair<Stats, Float>> = liveData {
        val stats = repository.getStatsFromRecipe(id)
        val user = repository.getUser(true)
        val rec = Recommender(user?.age, user?.height, user?.weight, user?.gender, user?.lifeStyle)
        val data = Pair(stats, rec.bmr)
        emit(data)
    }

    val recipeIngredients: LiveData<List<RecipeIngredients>> = liveData {
        val data = repository.getRecipeIngredients(id)
        emit(data)
    }

    fun saveRecipe(recipe: Recipe, save: Boolean) {
        uiScope.launch {
            repository.setFavRecipe(recipe, save)
        }
        _recipe.value = recipe.apply {
            isSaved = save
        }
    }

    fun saveRating(recipe: Recipe, rating: Int) {
        uiScope.launch {
            repository.setRecipeRating(recipe, rating)
        }
    }

    fun getNutrients(bmr: Float): List<Int> {
        var nutrients = listOf<Int>()
        try {
            nutrients = NUTRIENTS.keys.last { bmr > it }.run {
                NUTRIENTS[this]
            }!!
        } catch (e: NoSuchElementException) {
            nutrients = NUTRIENTS[2100]!!
        }
        return nutrients
    }
}