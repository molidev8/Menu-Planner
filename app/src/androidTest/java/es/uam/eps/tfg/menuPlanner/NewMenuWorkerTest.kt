package es.uam.eps.tfg.menuPlanner

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.runner.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import es.uam.eps.tfg.menuPlanner.work.NewMenuDataWorker
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewMenuWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

//    @Test
//    fun testNewMenuWorker() {
//        val worker = TestListenableWorkerBuilder<NewMenuDataWorker>(context).build()
//        runBlocking {
//            val result = worker.doWork()
//            assertThat(result, `is`(ListenableWorker.Result.success()))
//        }
//    }
}