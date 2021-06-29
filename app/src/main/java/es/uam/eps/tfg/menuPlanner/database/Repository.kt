package es.uam.eps.tfg.menuPlanner.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import es.uam.eps.tfg.menuPlanner.ServiceLocator
import es.uam.eps.tfg.menuPlanner.database.local.AppDatabase
import es.uam.eps.tfg.menuPlanner.database.remote.FirebaseRepository
import es.uam.eps.tfg.menuPlanner.menu.BREAKFAST
import es.uam.eps.tfg.menuPlanner.menu.DINNER
import es.uam.eps.tfg.menuPlanner.menu.LUNCH
import es.uam.eps.tfg.menuPlanner.menu.NUTRIENTS
import es.uam.eps.tfg.menuPlanner.network.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import kotlin.NoSuchElementException

val cuisines = listOf<String>("African", "American", "British", "Cajun", "Caribbean", "Chinese",
"Eastern European", "European", "French", "German", "Greek", "Indian", "Irish", "Italian", "Japanese"
,"Jewish", "Korean", "Latin American", "Mediterranean", "Mexican", "Middle Eastern", "Nordic",
"Southern", "Spanish", "Thai", "Vietnamese")

class Repository(private val firebase: FirebaseRepository,
                 private val appDatabase: AppDatabase
) : MenuRepository {

    override val authState: LiveData<FirebaseUser>
        get() = firebase.authState

    override suspend fun getUser(fromCache: Boolean): User? {
        return if (fromCache)
            appDatabase.appDao.getUser()
        else
            firebase.getUser()
    }

    override suspend fun setUser(user: User) {
        CoroutineScope(IO).launch {
            appDatabase.appDao.setUser(user)
        }
        firebase.setUser(user)
    }

    override suspend fun setRating(rating: Rating) {
        CoroutineScope(IO).launch {
            firebase.setRating(rating)
        }
    }

    override suspend fun getRating(): Rating? {
        return firebase.getRatings()
    }
    override suspend fun signInWithEmail(email: String, password: String): Boolean {
        return firebase.signInWithEmail(email, password)
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        firebase.signUpWithEmail(email, password)
    }

    override suspend fun getRecipesFromDay(day: String): List<Recipe> = appDatabase.appDao.getRecipesFromDay(day)

    override suspend fun getRecipe(id: Int): Recipe = appDatabase.appDao.getRecipe(id)

    override suspend fun getSavedRecipes(): List<Recipe> = appDatabase.appDao.getSavedRecipes()

    suspend fun resetCache() {
        appDatabase.clearAllTables()
    }

    override suspend fun getShoppingList(): List<ShoppingList> = appDatabase.appDao.getShoppingList()

    override suspend fun getRecipeIngredients(id: Int) = appDatabase.appDao.getRecipeIngredients(id)

    override suspend fun updateShoppingList(shoppingList: ShoppingList) {
        appDatabase.appDao.updateIngredient(shoppingList.name, shoppingList.checked)
    }

    override suspend fun setFavRecipe(recipe: Recipe, save: Boolean) {
        CoroutineScope(IO).launch {
            appDatabase.appDao.updateRecipe(recipe.id_recipe, save)
        }
        firebase.saveFavRecipe(recipe.id_recipe)
    }

    override suspend fun setRecipeRating(recipe: Recipe, rating: Int) {
        CoroutineScope(IO).launch {
            appDatabase.appDao.updateRecipeRating(recipe.id_recipe, rating)
        }
        firebase.setRating(recipe.dishTypes, recipe.cuisines, rating)
    }

    suspend fun getRatings(): Rating? {
        return firebase.getRatings()
    }

    override suspend fun getStatsFromRecipe(id_recipe: Int): Stats = appDatabase.appDao.getStatsFromRecipe(id_recipe)

    override suspend fun recoverSavedRecipes() {
        withContext(IO) {
            var recipeIds = ""
            val ids = firebase.getSavedRecipes()
            val recipes = mutableListOf<Recipe>()
            if (ids.isNullOrEmpty())
                return@withContext

            ids.forEach { id ->
                recipeIds += id
                if (ids.last() != id) {
                    recipeIds += ","
                }
            }
            val getRecipesDeferred: Deferred<List<RecipeSearch>> =
                SpoonacularApi.retrofitService.getRecipesInfo(ids = recipeIds)
            val results = getRecipesDeferred.await()

            results.forEach {

                var instructions = ""
                it.analyzedInstructions.first().steps.forEach { step ->
                    instructions += step.number.toString() + ". " + step.step + "\n"
                }

                recipes.add(
                    Recipe(
                        it.id,
                        it.title,
                        it.dishTypes.toString(),
                        instructions,
                        it.sourceUrl,
                        it.image,
                        true,
                        it.readyInMinutes ?: 0,
                        it.servings,
                        it.cuisines ?: mutableListOf()
                    )
                )
            }
            appDatabase.appDao.setRecipes(recipes)
        }
    }

    @ExperimentalStdlibApi
    override suspend fun refreshMenu(feature: String?, category: String, bmr: Float) {
        var getRecipesDeferred: Deferred<Results>
        var nutrients = listOf<Int>()
        var results: Results

        val cal = when(category) {
            "breakfast" -> bmr * BREAKFAST
            "lunch" -> bmr * LUNCH
            else -> bmr * DINNER
        }

        nutrients = try {
            NUTRIENTS.keys.last { bmr > it }.run {
                NUTRIENTS[this]
            }!!
        } catch (e: NoSuchElementException) {
            NUTRIENTS[2100]!!
        }

        withContext(IO) {
            try {
                var i = 0
                do {
                    if (feature != null) {
                        getRecipesDeferred = SpoonacularApi.retrofitService.getRecipesId(
                            cuisine = feature,
                            type = category,
                            calories = cal * 1.1F,
                            protein = nutrients[0],
                            fat = nutrients[1],
                            carbs = nutrients[2]
                        )
                    } else {
                        getRecipesDeferred = SpoonacularApi.retrofitService.getRecipesId(
                            cuisine = cuisines.random(),
                            type = category,
                            calories = cal * 1.1F,
                            protein = nutrients[0],
                            fat = nutrients[1],
                            carbs = nutrients[2]
                        )
                    }
                    results = getRecipesDeferred.await()
                    i++
                    if (results.totalResults < 4 && i > 2) {
                        getRecipesDeferred = SpoonacularApi.retrofitService.getRecipesId(
                            cuisine = "",
                            type = category,
                            calories = cal * 1.1F,
                            protein = nutrients[0],
                            fat = nutrients[1],
                            carbs = nutrients[2]
                        )
                    }

                } while(results.totalResults < 4)

                saveRecipes(results.results, category)

            } catch(e: Exception) {
                Log.d("Menu", "failure fetching recipes ", e)
            }
        }
    }

    private suspend fun getIngredientsFromRecipe(id: Int): List<IngredientInfo> {
        var getIngredientsDeferred: Deferred<Ingredients>
        var ingredients: Ingredients = Ingredients(mutableListOf())

        withContext(IO) {
            try {
                getIngredientsDeferred = SpoonacularApi.retrofitService.getIngredientsFromRecipe(id)
                ingredients = getIngredientsDeferred.await()
            } catch(e: Exception) {
                Log.d("Menu", "failure fetching ing ", e)
            }
        }

        return ingredients.ingredients
    }

    @ExperimentalStdlibApi
    private suspend fun saveRecipes(rcp: List<RecipeInfo>, category: String){
        val stats = mutableListOf<Stats>()
        val rows = mutableListOf<RecipeUserCross>()
        val recipes = rcp.asDatabaseModel(category)
        val days = mutableListOf(
            listOf("saturday"),
            listOf("monday", "wednesday"),
            listOf("tuesday", "thursday"),
            listOf("friday", "sunday")
        )

        appDatabase.appDao.setRecipes(recipes)

        rcp.forEach { recipe ->

            val row = RecipeUserCross(recipe.id, firebase.getUserID(), days.first())
            rows.add(row)
//            val retorno = appDatabase.appDao.setRecipeUser(row)
//            Log.d("DEBUG", "Filas insertadas $retorno para los dias ${days.first()}")
            days.removeFirst()

            if (recipe.nutrition != null)
                stats.add(
                    Stats(
                        id_recipe = recipe.id,
                        calories = recipe.nutrition[0].amount,
                        protein = recipe.nutrition[1].amount,
                        fat = recipe.nutrition[2].amount,
                        carbs = recipe.nutrition[3].amount
                    )
                )

            getIngredientsFromRecipe(recipe.id).forEach { ing ->
                val id = UUID.randomUUID().toString()
                appDatabase.appDao.setIngredient(ing.asDatabaseModel(id))
                appDatabase.appDao.setRecipeIng(RecipeIngCross(recipe.id, id, ing.amount.metric.value, ing.amount.metric.unit))
            }

        }
        appDatabase.appDao.setRecipesUser(rows)
        appDatabase.appDao.setStats(stats)

    }

    suspend fun searchRecipes(query: String): List<Recipe>? {
        val recipes: List<Recipe>? = withContext(IO) {
            val getRecipesDeferred = SpoonacularApi.retrofitService.searchRecipes(query = query)

            try {
                val results = getRecipesDeferred.await()
                val endRecipes = results.results.asSearchResultsModel()
                appDatabase.appDao.setRecipes(endRecipes)
                endRecipes
            } catch (e: Exception) {
                Log.d("Search", "failure searching for recipes", e)
                null
            }
        }
        Log.d("Search", "${recipes?.first()}")
        return recipes
    }

    override fun signOut() {
        CoroutineScope(IO).launch {
            firebase.signOut()
            ServiceLocator.resetPreferences()
            resetCache()
        }
    }

    override fun deleteUser() {
        CoroutineScope(IO).launch {
            ServiceLocator.resetPreferences()
            resetCache()
            firebase.deleteUser()
        }
    }
}

