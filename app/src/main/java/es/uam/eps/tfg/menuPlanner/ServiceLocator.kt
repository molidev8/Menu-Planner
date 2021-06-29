package es.uam.eps.tfg.menuPlanner

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.database.Repository
import es.uam.eps.tfg.menuPlanner.database.local.AppDatabase
import es.uam.eps.tfg.menuPlanner.database.remote.FirebaseRepository

object ServiceLocator {

    private val lock = Any()
    private var context: Context? = null
    private var database: AppDatabase? = null
    @Volatile
    var repository: MenuRepository? = null

    fun provideRepository(context: Context): MenuRepository {
        synchronized(this) {
            this.context = context
            return repository ?: createRepository(context)
        }
    }

    private fun createRepository(context: Context): MenuRepository {
        val newRepo = Repository(FirebaseRepository(), createLocalDataSource(context))
        repository = newRepo
        return newRepo
    }

    private fun createLocalDataSource(context: Context) : AppDatabase {
        val database = database ?: createDatabase(context)
        return database
    }

    private fun createDatabase(context: Context): AppDatabase {
        val result = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app")
                    .build()

        database = result
        return result
    }

    fun resetPreferences() {
        val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        with(preferences?.edit()) {
            this?.clear()
            this?.apply()
        }
    }

    @VisibleForTesting
    fun resetApplication() {
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }
            repository?.signOut()
            val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
            with(preferences?.edit()) {
                this?.clear()
                this?.apply()
            }
            database = null
            repository = null
        }
    }


}