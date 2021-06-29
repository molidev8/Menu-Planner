package es.uam.eps.tfg.menuPlanner.database.local

import androidx.room.*
import es.uam.eps.tfg.menuPlanner.database.*


@Database(entities = [User::class, Recipe::class, Ingredient::class, RecipeIngCross::class, RecipeUserCross::class, Stats::class, RecipeStatsCross::class],
    views = arrayOf(ShoppingList::class, RecipeIngredients::class),version = 1, exportSchema = false)
@TypeConverters(
    GenderTypeConverter::class, LifeStyleTypeConverter::class, DaysConverter::class)

abstract class AppDatabase: RoomDatabase() {
    abstract val appDao: AppDao
}