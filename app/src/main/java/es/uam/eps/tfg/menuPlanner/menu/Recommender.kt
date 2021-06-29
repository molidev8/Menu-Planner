package es.uam.eps.tfg.menuPlanner.menu

import android.util.Log
import es.uam.eps.tfg.menuPlanner.database.Gender
import es.uam.eps.tfg.menuPlanner.database.LifeStyle

const val BREAKFAST = 0.178F
const val LUNCH = 0.47F
const val DINNER = 0.352F
// calories -> (protein, fat, carbohydrate)
val NUTRIENTS = mutableMapOf<Int, List<Int>>(
    1200 to mutableListOf(90, 33, 125),
    1500 to mutableListOf(112, 42, 75),
    1800 to mutableListOf(135, 50, 202),
    2100 to mutableListOf(158, 58, 236),
    2400 to mutableListOf(180, 67, 270),
    2700 to mutableListOf(202, 75, 303),
    3000 to mutableListOf(225, 83, 338)
)

class Recommender(private val age: Int?,
                  private val height: Int?,
                  private val weight: Int?,
                  private val gender: Gender?,
                  private val lifeStyle: LifeStyle?
) {

    var bmr: Float = 0.0F

    init {
        bmr = when (gender) {
            Gender.MAN -> 88.362F + (13.397F * weight!!) + (4.799F * height!!) - (5.677F * age!!)
            Gender.WOMAN ->    447.593F + (9.247F * weight!!) + (3.098F * height!!) - (4.330F * age!!)
            else -> 0.0F
        }

        bmr = when (lifeStyle) {
            LifeStyle.SEDENTARY -> bmr * 1.2F
            LifeStyle.M_ACTIVE -> bmr * 1.55F
            LifeStyle.ACTIVE -> bmr * 1.725F
            else -> bmr * 0.0F
        }

        if (bmr == 0.0F)
            Log.d("Menu", "Invalid bmr result")
    }

    suspend fun getBestFeature(features: Map<String, Int>?, type: String): String? {

        if (bmr == 0.0F)
            return null

        if (features != null){
            val scores = mutableMapOf<String, Float>()
            val tot = features.values.sum()
            features.forEach { entry ->
                val score = (entry.value * tot).toFloat() / features.size
                scores[entry.key] = score
            }

            val bestFeature = features.maxBy { it.value }?.key
            Log.d("Menu", "Best feature for $type is $bestFeature")
            if (bestFeature != null) {
                return bestFeature
            }
        }
        return null
    }
}
