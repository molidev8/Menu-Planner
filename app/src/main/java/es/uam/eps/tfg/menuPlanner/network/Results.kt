package es.uam.eps.tfg.menuPlanner.network

import es.uam.eps.tfg.menuPlanner.database.Ingredient
import es.uam.eps.tfg.menuPlanner.database.Recipe


data class Results(
    val results: List<RecipeInfo>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
) {
    override fun toString(): String {
        return "Results(results=$results, offset=$offset, number=$number, totalResults=$totalResults)"
    }
}

data class Metric(
    val metric: MetricValue
)

data class MetricValue(
    val unit: String,
    val value: Float
)

data class RecipeInfo(
    val id: Int,
    val title: String,
    val instructions: String?,
    val servings: Int,
    val sourceUrl: String,
    val readyInMinutes: Int?,
    val image: String,
    val cuisines: List<String>?,
    val dishTypes: List<String>?,
    val nutrition: List<CaloricBreakdown>?,
    val analyzedInstructions: List<Instructions>
)

data class Instructions(
    val steps: List<Steps>
)

data class Steps(
    val number: Int,
    val step: String
)

data class Ingredients(
    val ingredients: List<IngredientInfo>
)

data class IngredientInfo(
    val name: String,
    val amount: Metric
)

data class CaloricBreakdown(
    val title: String,
    val amount: Float,
    val unit: String
)


data class RecipeSearch(
    val id: Int,
    val title: String,
    val servings: Int,
    val sourceUrl: String,
    val readyInMinutes: Int?,
    val image: String,
    val cuisines: List<String>?,
    val dishTypes: List<String>?,
    val analyzedInstructions: List<Instructions>
)

fun IngredientInfo.asDatabaseModel(id: String): Ingredient {

    return Ingredient(
        id,
        this.name,
        false
    )
}

fun List<RecipeInfo>.asDatabaseModel(category: String): List<Recipe> {

    val recipes = mutableListOf<Recipe>()

    forEach { recipe ->
        var instructions = ""
        recipe.analyzedInstructions.first().steps.forEach { step ->
            instructions += step.number.toString() + ". " + step.step + "\n"
        }

        val finalRecipe = Recipe(
            recipe.id,
            recipe.title,
            category,
            instructions,
            recipe.sourceUrl,
            recipe.image,
            false,
            recipe.readyInMinutes ?: 0,
            recipe.servings,
            recipe.cuisines ?: mutableListOf()
        )
        recipes.add(finalRecipe)
    }

    return recipes
}

fun List<RecipeInfo>.asSearchResultsModel(): List<Recipe> {
    val recipes = mutableListOf<Recipe>()

    forEach { recipe ->
        var instructions = ""
        recipe.analyzedInstructions.first().steps.forEach { step ->
            instructions += step.number.toString() + ". " + step.step + "\n"
        }

        recipes.add(
            Recipe(
                recipe.id,
                recipe.title,
                instructions,
                recipe.sourceUrl,
                recipe.image,
                recipe.readyInMinutes ?: 0,
                recipe.servings,
                recipe.cuisines ?: mutableListOf()
            )
        )
    }

    return recipes
}