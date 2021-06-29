package es.uam.eps.tfg.menuPlanner.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import es.uam.eps.tfg.menuPlanner.database.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FirebaseRepository (
    private val db: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val USERS = "users"
    private val RATINGS = "ratings"
    private val RECIPES = "recipes"
    private val FIRESTORE = "FIRESTORE"

    fun signOut() {
        auth.signOut()
    }

    val authState = object : LiveData<FirebaseUser>() {
        private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            value = firebaseAuth.currentUser
        }
        override fun onActive() {
            auth.addAuthStateListener(authStateListener)
        }

        override fun onInactive() {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    fun getUserID() = auth.uid!!

    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(FIRESTORE, "Usuario logeado ok")
            true
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            false
        } catch (e: FirebaseAuthInvalidUserException) {
            false
        }
    }

    suspend fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(FIRESTORE, "Usuario registrado ok")
    }

    suspend fun getUser(): User? {
        var user: UserFromFirebase?
        try {
            val documentSnapshot = db.collection(USERS).document(auth.uid!!).get().await()
            user = documentSnapshot.toObject()
            Log.d(FIRESTORE, "DocumentSnapshot data: ${documentSnapshot.data}")
        } catch (e: Exception) {
            user = null
            Log.d(FIRESTORE, "get failed with ", e)
        }
        return user?.toUser()
    }

    suspend fun getSavedRecipes(): List<Int>? {
        var user: UserFromFirebase?
        try {
            val documentSnapshot = db.collection(USERS).document(auth.uid!!).get().await()
            user = documentSnapshot.toObject()
            Log.d(FIRESTORE, "DocumentSnapshot data: ${documentSnapshot.data}")
        } catch (e: Exception) {
            user = null
            Log.d(FIRESTORE, "get failed with ", e)
        }
        return user?.savedRecipes
    }

    suspend fun setUser(user: User) {

        db.collection(USERS).document(auth.uid!!).set(user, SetOptions.merge())
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Log.d(FIRESTORE, "user saved successfully")
                else if(task.isCanceled)
                    Log.d(FIRESTORE, "save user failed ", task.exception)
            }
    }

    suspend fun setRating(rating: Rating) {

        db.collection(RATINGS).document(auth.uid!!).set(rating)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Log.d(FIRESTORE, "rating saved successfully")
                else if(task.isCanceled)
                    Log.d(FIRESTORE, "save rating failed ", task.exception)
            }
    }

    suspend fun saveFavRecipe(id: Int) {
        db.collection(USERS).document(auth.uid!!).update("savedRecipes", FieldValue.arrayUnion(id))
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Log.d(FIRESTORE, "fav recipe added")
                else if(task.isCanceled)
                    Log.d(FIRESTORE, "fav recipe failed ", task.exception)
            }
    }

    fun deleteUser() {
        val aux = auth.uid!!

        db.collection(USERS).document(aux).delete()
            .addOnSuccessListener { Log.d(FIRESTORE, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(FIRESTORE, "Error deleting document", e) }

        db.collection(RATINGS).document(aux).delete()
            .addOnSuccessListener { Log.d(FIRESTORE, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(FIRESTORE, "Error deleting document", e) }


        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful)
                Log.d(FIRESTORE, "User account deleted")
        }

        auth.signOut()
    }

    /*suspend fun setRecipes(recipes: List<Recipe>) {
        db.runBatch { batch ->
            recipes.forEach { recipe ->
                batch.set(db.collection(USERS).document(auth.uid!!).collection(RECIPES).document(
                    recipe.id_recipe.toString()), recipe)
            }
        }.addOnCompleteListener { task ->
            if (task.isSuccessful)
                Log.d(FIRESTORE, "recipes saved successfully")
            else if(task.isCanceled)
                Log.d(FIRESTORE, "save recipes failed ", task.exception)
        }
    }*/

    suspend fun getRatings(): Rating? {
        var rating: Rating? = null
        try {
            if (auth.uid != null) {
                val documentSnapshot = db.collection(RATINGS).document(auth.uid!!).get().await()
                rating = documentSnapshot.toObject()
                Log.d(FIRESTORE, "DocumentSnapshot data: ${documentSnapshot.data}")
            }
        } catch (e: Exception) {
            rating = null
            Log.d(FIRESTORE,"get failed with ", e)
        }
        return rating
    }

    suspend fun setRating(dishType: String, cuisines: List<String>, rating: Int) {

        cuisines.forEach {
            val key = "$dishType.$it"

            try {
                db.collection(RATINGS).document(auth.uid!!).update(mapOf(
                    key to rating
                ))
                Log.d(FIRESTORE, "rating updated")
            } catch (e: Exception) {
                Log.d(FIRESTORE, "rating update failed ", e)
            }

        }

    }
}