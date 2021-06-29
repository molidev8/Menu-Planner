package es.uam.eps.tfg.menuPlanner.database.local

import androidx.room.*
import es.uam.eps.tfg.menuPlanner.database.*

@Dao
interface AppDao {

    //User

    @Query("select * from User")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUser(user: User)

    //Recipe

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRecipes(recipes: List<Recipe>)

    @Query("update Recipe set isSaved = (:isSaved) where id_recipe = (:id)")
    suspend fun updateRecipe(id: Int, isSaved: Boolean)

    @Query("update Recipe set rating = (:rating) where id_recipe = (:id)")
    suspend fun updateRecipeRating(id: Int, rating: Int)

    @Query("select * from Recipe as R, RecipeUserCross as RU where R.id_recipe = RU.id_recipe and id_day like (:day)")
    suspend fun getRecipesFromDay(day: String): List<Recipe>

    @Query("select * from Recipe where id_recipe = (:id)")
    suspend fun getRecipe(id: Int): Recipe

    @Query("select * from Recipe where isSaved=1")
    suspend fun getSavedRecipes(): List<Recipe>

    @Query("delete from Recipe")
    suspend fun deleteRecipes()

    //Ingredient

    @Insert()
    suspend fun setIngredient(ingredient: Ingredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setIngredients(ingredient: List<Ingredient>)

    @Query("update Ingredient set checked = (:checked) where name = (:id)")
    suspend fun updateIngredient(id: String, checked: Boolean)

    @Query("delete from Ingredient")
    suspend fun deleteIngredients()

    //Stats

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStats(stats: List<Stats>)

    @Query("delete from Stats")
    suspend fun deleteStats()

    @Query("select * from Stats where id_recipe = (:id_recipe)")
    suspend fun getStatsFromRecipe(id_recipe: Int): Stats

    //RecipeIngCross

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRecipeIng(recipeIngCross: RecipeIngCross)

    @Query("delete from RecipeIngCross")
    suspend fun deleteRecipeIng()

    //RecipeUserCross

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setRecipeUser(recipeUserCross: RecipeUserCross): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setRecipesUser(recipeUserCross: List<RecipeUserCross>)

    @Query("delete from RecipeUserCross")
    suspend fun deleteRecipeUser()

    //RecipeStatsCross

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setRecipeStats(recipeStatsCross: RecipeStatsCross)

    @Query("delete from RecipeStatsCross")
    suspend fun deleteRecipeStats()

    //Mixed

    @Query("select * from ShoppingList")
    suspend fun getShoppingList(): List<ShoppingList>

    @Query("select * from RecipeIngredients where id_recipe = (:id)")
    suspend fun getRecipeIngredients(id: Int): List<RecipeIngredients>
}