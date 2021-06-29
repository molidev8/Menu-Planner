package es.uam.eps.tfg.menuPlanner.database

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenderTypeConverter {
    @TypeConverter
    fun enumToString(gender: Gender) = gender.toString()

    @TypeConverter
    fun stringToEnum(gender: String) = when(gender) {
        Gender.MAN.toString() -> Gender.MAN
        Gender.WOMAN.toString() -> Gender.MAN
        else -> Gender.NONE
    }
}

class LifeStyleTypeConverter {
    @TypeConverter
    fun enumToString(lifeStyle: LifeStyle) = lifeStyle.toString()

    @TypeConverter
    fun stringToEnum(lifeStyle: String) = when(lifeStyle) {
        LifeStyle.ACTIVE.toString() -> LifeStyle.ACTIVE
        LifeStyle.M_ACTIVE.toString() -> LifeStyle.M_ACTIVE
        LifeStyle.SEDENTARY.toString() -> LifeStyle.SEDENTARY
        else -> LifeStyle.NONE
    }
}

class DaysConverter {

    @TypeConverter
    fun listToString(id_day: List<String>): String {
        return id_day.joinToString()
    }

    @TypeConverter
    fun StringToList(days: String): List<String> {
        return days.split(",")
    }
}

@Entity
data class Recipe(
    @PrimaryKey val id_recipe: Int, val title: String, val dishTypes: String,
    val instructions: String, val sourceUrl: String, val image: String,
    var isSaved: Boolean = false, val readyInMinutes: Int, val servings: Int, val cuisines: List<String> = mutableListOf(), var rating: Int = 0
) {
    constructor() : this(0, "", " ", "", "", "", false, 0, 0, mutableListOf())
    constructor(
        id: Int,
        title: String,
        instructions: String,
        sourceUrl: String,
        image: String,
        readyInMinutes: Int,
        servings: Int,
        cuisines: List<String>
    ) : this(id, title, "", instructions, sourceUrl, image, false, readyInMinutes, servings, cuisines)
}

@Entity
data class Ingredient(
    @PrimaryKey val id_ingredient: String,
    val name: String,
    var checked: Boolean = false){

    constructor(): this("", ",", false)
}

@Entity
data class User(
    @PrimaryKey var id_user: String,
    var username: String,
    var age: Int,
    var height: Int,
    var weight: Int,
    var gender: Gender,
    var lifeStyle: LifeStyle
) {
    constructor() : this(
        "", "", 0, 0, 0,
        Gender.NONE,
        LifeStyle.NONE
    )

    fun toUserFromFirebase(savedRecipes: List<Int> = mutableListOf()): UserFromFirebase =
        UserFromFirebase(id_user, username, age, height, weight, gender, lifeStyle, savedRecipes)
}

@Entity
data class Stats(
    @PrimaryKey(autoGenerate = true) val id_stats: Int,
    val id_recipe: Int,
    val calories: Float,
    val protein: Float,
    val fat: Float,
    val carbs: Float
) {
    constructor(id_recipe: Int, calories: Float, protein: Float, fat: Float, carbs: Float) : this(0, id_recipe, calories, protein, fat, carbs)
}

data class UserFromFirebase(
    var id_user: String,
    var username: String,
    var age: Int,
    var height: Int,
    var weight: Int,
    var gender: Gender,
    var lifeStyle: LifeStyle,
    var savedRecipes: List<Int>
) {
    constructor() : this(
        "", "", 0, 0, 0,
        Gender.NONE,
        LifeStyle.NONE,
        mutableListOf()
    )

    fun toUser(): User = User(
        id_user, username, age, height, weight, gender, lifeStyle
    )
}

@Entity(primaryKeys = ["id_recipe", "id_stats"])
data class RecipeStatsCross(
    val id_recipe: Int,
    val id_stats: Int
)

@Entity(primaryKeys = ["id_recipe", "id_ingredient"])
data class RecipeIngCross(
    val id_recipe: Int,
    val id_ingredient: String,
    val amount: Float,
    val unit: String
)

@Entity(primaryKeys = ["id_recipe", "id_user"])
data class RecipeUserCross(
    val id_recipe: Int,
    val id_user: String,
    val id_day: List<String>
)

data class UserWithRecipes(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id_user",
        entityColumn = "id_recipe",
        associateBy = Junction(RecipeUserCross::class)
    )
    val recipes: List<Recipe>
)

data class RecipeWithIng(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id_recipe",
        entityColumn = "id_ingredient",
        associateBy = Junction(RecipeIngCross::class)
    )
    val ingredients: List<Ingredient>
)

@DatabaseView("select Ingredient.id_ingredient, name, checked, amount, unit, id_day " +
        "from Ingredient, RecipeIngCross, Recipe, RecipeUserCross " +
        "where RecipeUserCross.id_recipe = Recipe.id_recipe and Recipe.id_recipe = RecipeIngCross.id_recipe " +
        "and RecipeIngCross.id_ingredient = Ingredient.id_ingredient")
data class ShoppingList(
    val id_ingredient: Int,
    val name: String,
    var checked: Boolean,
    val amount: Float,
    val unit: String,
    val id_day: List<String>
)

@DatabaseView("select Ingredient.id_ingredient, name, amount, unit, Recipe.id_recipe from Ingredient, " +
        "RecipeIngCross, Recipe where Recipe.id_recipe = RecipeIngCross.id_recipe" +
        " and RecipeIngCross.id_ingredient = Ingredient.id_ingredient")
data class RecipeIngredients(
    val id_ingredient: Int,
    val name: String,
    val amount: Float,
    val unit: String,
    val id_recipe: Int
)



data class Rating(
    val breakfast: MutableMap<String, Int>,
    val dinner: MutableMap<String, Int>,
    val lunch: MutableMap<String, Int>
){
    constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf())

    override fun toString(): String {
        return "Rating(breakfast=$breakfast, dinner=$dinner, lunch=$lunch)"
    }
}

enum class Gender {
    WOMAN, MAN, NONE
}

enum class LifeStyle {
    SEDENTARY, M_ACTIVE, ACTIVE, NONE
}