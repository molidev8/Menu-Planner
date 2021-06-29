package es.uam.eps.tfg.menuPlanner.menu

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.database.Rating
import es.uam.eps.tfg.menuPlanner.database.User
import es.uam.eps.tfg.menuPlanner.database.Repository
import es.uam.eps.tfg.menuPlanner.util.viewModelFactory2
import kotlinx.coroutines.*

class MainMenuViewModel(private val repository: MenuRepository, application: Application) : AndroidViewModel(application) {

    companion object {
        val FACTORY = viewModelFactory2(::MainMenuViewModel)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var user: User? = null
    private var rating: Rating? = null
    private val recommender: Recommender by lazy {
        Recommender(
                user?.age,
                user?.height,
                user?.weight,
                user?.gender,
                user?.lifeStyle
            )
    }
    private var preferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences("pref", Context.MODE_PRIVATE)

    init {
        if (!preferences.getBoolean("isSaved", false)) {
            Log.d("Menu","Loading menu for the first time")
            uiScope.launch {
                val tempUser = async { getUser() }
                val tempRating = async { getRatings() }
                user = tempUser.await()
                rating = tempRating.await()
                Log.d("Menu", user.toString())
                Log.d("Menu", rating.toString())
                refreshRecommendation()
                repository.recoverSavedRecipes()
                with(preferences.edit()){
                    putBoolean("isSaved", true)
                    apply()
                }
                _menuCreated.value = true
            }
        }
    }

    private val _menuCreated = MutableLiveData<Boolean>()
    val menuCreated: LiveData<Boolean>
        get() = _menuCreated

    private suspend fun refreshRecommendation() {
        withContext(Dispatchers.Default) {
            val breakfast = async {
                val breakfast = recommender.getBestFeature(rating?.breakfast, "breakfast")
                repository.refreshMenu(breakfast, "breakfast", recommender.bmr)
            }
            val lunch = async {
                val lunch = recommender.getBestFeature(rating?.lunch, "lunch")
                repository.refreshMenu(lunch, "lunch", recommender.bmr)
            }
            val dinner = async {
                val dinner = recommender.getBestFeature(rating?.dinner, "dinner")
                repository.refreshMenu(dinner, "dinner", recommender.bmr)
            }

            breakfast.await()
            lunch.await()
            dinner.await()
        }
    }


    private suspend fun getUser(): User? {
        return withContext(Dispatchers.IO) {
            repository.getUser(true)
        }
    }

    private suspend fun getRatings(): Rating? {
        return withContext(Dispatchers.IO) {
            (repository as Repository).getRatings()
        }
    }

}