package es.uam.eps.tfg.menuPlanner

import android.app.Application
import android.os.Build
import androidx.work.*
import es.uam.eps.tfg.menuPlanner.database.MenuRepository
import es.uam.eps.tfg.menuPlanner.work.NewMenuDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MenuApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    val repository: MenuRepository
        get() = ServiceLocator.provideRepository(this)

    override fun onCreate() {
        super.onCreate()
        instance = this

        applicationScope.launch {
            setupRecurringWork()
        }
    }

    companion object {
        lateinit var instance: MenuApplication private set
    }

    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<NewMenuDataWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints).build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            NewMenuDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )

    }
}