package es.uam.eps.tfg.menuPlanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val repository = (requireContext().applicationContext as MenuApplication).repository


            val signOutPref = findPreference<Preference>("signout")
            signOutPref?.setOnPreferenceClickListener {
                repository.signOut()
                resetApp()
            }

            val eraseData = findPreference<Preference>("removeData")
            eraseData?.setOnPreferenceClickListener {
                repository.deleteUser()
                resetApp()
            }
        }

        private fun resetApp(): Boolean {
            val reset = context?.packageName?.let { it1 ->
                context?.packageManager?.getLaunchIntentForPackage(
                    it1
                )
            }

            reset?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            reset?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(reset)
            return true
        }
    }
}