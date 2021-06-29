package es.uam.eps.tfg.menuPlanner.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import es.uam.eps.tfg.menuPlanner.MenuApplication
import es.uam.eps.tfg.menuPlanner.database.User
import es.uam.eps.tfg.menuPlanner.database.Rating
import es.uam.eps.tfg.menuPlanner.database.Repository
import es.uam.eps.tfg.menuPlanner.menu.Recommender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class NewMenuDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val repository = (appContext.applicationContext as MenuApplication).repository

    private var user: User? = null
    private var rating: Rating? = null
    private lateinit var recommender: Recommender

    companion object {
        const val WORK_NAME = "es.uam.eps.tfg.menuPlanner.work.NewMenuDataWorker"
    }

    override suspend fun doWork(): Result {

        withContext(IO){
            val tempRating = async { getRatings() }
            val tempUser = async { getUser() }
            user = tempUser.await()
            rating = tempRating.await()
            recommender = Recommender(
            user?.age,
            user?.height,
            user?.weight,
            user?.gender,
            user?.lifeStyle
        )
        }

        Log.d("Menu", user.toString())
        Log.d("Menu", rating.toString())
        refreshRecommendation()

        return Result.success()
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

    private suspend fun refreshRecommendation() {
        withContext(Dispatchers.Default) {

            (repository as Repository).resetCache()

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


            user?.let { repository.setUser(it) }
        }
    }
}