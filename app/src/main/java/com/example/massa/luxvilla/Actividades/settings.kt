package com.example.massa.luxvilla.Actividades

import android.content.Intent
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.Menu
import android.view.MenuItem
import com.example.massa.luxvilla.R
import com.google.firebase.messaging.FirebaseMessaging
import com.lapism.searchview.SearchHistoryTable
import com.lapism.searchview.SearchItem
import java.util.ArrayList

/**
 * Created by massa on 27/10/2017.
 */
class settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                UserPreferenceFragment()).commit()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    class UserPreferenceFragment : PreferenceFragment() {

        internal var preferenceclear: Preference? = null
        internal var preferencesobre: Preference? = null
        internal var switchPreferencenightmode: SwitchPreference? = null
        internal var switchPreferencenotifications: CheckBoxPreference? = null
        internal var msearchHistoryTable: SearchHistoryTable? = null
        internal var sugestions: MutableList<SearchItem>? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

            preferenceclear = preferenceScreen.findPreference(resources.getString(R.string.clearhistory))

            preferenceclear?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                sugestions = ArrayList()
                sugestions?.clear()
                msearchHistoryTable = SearchHistoryTable(activity)
                msearchHistoryTable?.clearDatabase()
                Snackbar.make(view!!, "histórico de busca eliminado", Snackbar.LENGTH_LONG).show()
                true
            }

            switchPreferencenotifications = preferenceScreen.findPreference(resources.getString(R.string.notificaçãoes)) as CheckBoxPreference
            switchPreferencenotifications?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val switched = (preference as SwitchPreference)
                        .isChecked
                if (switched) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("todos")
                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic("todos")
                }
                true
            }

            switchPreferencenightmode = preferenceScreen.findPreference(resources.getString(R.string.night_mode)) as SwitchPreference
            switchPreferencenightmode?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                val switched = (preference as SwitchPreference)
                        .isChecked
                if (switched) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                activity.onBackPressed()
                true
            }

            preferencesobre = preferenceScreen.findPreference(resources.getString(R.string.sobre))
            preferencesobre?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(activity, Sobre::class.java))
                true
            }

        }

    }
}
