package es.uam.eps.tfg.menuPlanner.database

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser


interface MenuRepository {

    val authState: LiveData<FirebaseUser>

    suspend fun getUser(fromCache: Boolean): User?

    suspend fun setUser(user: User)

    suspend fun setRating(rating: Rating)

    suspend fun getRating(): Rating?

    suspend fun signInWithEmail(email: String, password: String): Boolean

    suspend fun signUpWithEmail(email: String, password: String)

    suspend fun getRecipesFromDay(day: String): List<Recipe>

    suspend fun getRecipe(id: Int): Recipe

    suspend fun getSavedRecipes(): List<Recipe>

    suspend fun getShoppingList(): List<ShoppingList>

    suspend fun updateShoppingList(shoppingList: ShoppingList)

    suspend fun getRecipeIngredients(id: Int): List<RecipeIngredients>

    suspend fun recoverSavedRecipes()

    suspend fun refreshMenu(feature: String?, category: String, bmr: Float)

    suspend fun setFavRecipe(recipe: Recipe, save: Boolean)

    suspend fun setRecipeRating(recipe: Recipe, rating: Int)

    suspend fun getStatsFromRecipe(id_recipe: Int): Stats

    fun signOut()

    fun deleteUser()
}