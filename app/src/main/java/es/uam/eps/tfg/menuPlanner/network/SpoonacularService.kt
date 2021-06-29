package es.uam.eps.tfg.menuPlanner.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import es.uam.eps.tfg.menuPlanner.R
import es.uam.eps.tfg.menuPlanner.util.Strings
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.spoonacular.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    this.level = HttpLoggingInterceptor.Level.HEADERS
}

val client: OkHttpClient = OkHttpClient.Builder().apply {
    this.addInterceptor(interceptor)
}.build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

interface SpoonacularServiceApi {

    private val KEY: String
        get() = Strings.get(R.string.api_key)

    @GET("recipes/complexSearch")
    fun getRecipesId(
        @Query("apiKey") key: String = KEY,
        @Query("cuisine") cuisine: String,
        @Query("type") type: String,
        @Query("maxCalories") calories: Float,
        @Query("maxProtein") protein: Int,
        @Query("maxFat") fat: Int,
        @Query("maxCarbs") carbs: Int,
        @Query("number") nRecipes: Int = 4,
        @Query("addRecipeNutrition") nutrition: Boolean = true,
        @Query("addRecipeInformation") information: Boolean = true,
        @Query("instructionsRequired") instructions: Boolean = true
    ): Deferred<Results>

    @GET("recipes/{id}/ingredientWidget.json")
    fun getIngredientsFromRecipe(
        @Path("id") id: Int,
        @Query("apiKey") key: String = KEY
    ): Deferred<Ingredients>

    @GET("recipes/informationBulk")
    fun getRecipesInfo(
        @Query("apiKey") key: String = KEY,
        @Query("ids") ids: String,
        @Query("includeNutrition") nutrition: String = "true"
    ): Deferred<List<RecipeSearch>>

    @GET("recipes/complexSearch")
    fun searchRecipes(
        @Query("apiKey") key: String = KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("addRecipeInformation") information: Boolean = true,
        @Query("instructionsRequired") instructions: Boolean = true
    ): Deferred<Results>
}

object SpoonacularApi {
    val retrofitService: SpoonacularServiceApi by lazy {
        retrofit.create(SpoonacularServiceApi::class.java)
    }
}