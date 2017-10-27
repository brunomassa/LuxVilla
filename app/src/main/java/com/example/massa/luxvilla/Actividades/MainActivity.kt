package com.example.massa.luxvilla.Actividades

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.transition.ChangeBounds
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.example.massa.luxvilla.R
import com.example.massa.luxvilla.separadores.separadoraveiro
import com.example.massa.luxvilla.separadores.separadorbraga
import com.example.massa.luxvilla.separadores.separadorporto
import com.example.massa.luxvilla.separadores.separadortodas
import com.example.massa.luxvilla.sqlite.BDAdapter
import com.example.massa.luxvilla.utils.firebaseutils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.lapism.searchview.SearchAdapter
import com.lapism.searchview.SearchHistoryTable
import com.lapism.searchview.SearchItem
import com.lapism.searchview.SearchView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*
import java.util.*

/**
 * Created by massa on 26/10/2017.
 */
class MainActivity : AppCompatActivity() {

    internal val SEPARADOR_TODAS = 0
    internal val SEPARADOR_AVEIRO = 1
    internal val SEPARADOR_BRAGA = 2
    internal val SEPARADOR_PORTO = 3
    internal var adapter: BDAdapter? = null
    internal val PREFSNAME = "FAVS"
    internal var sharedPreferencesapp: SharedPreferences? = null
    internal var sharedPreferenceslikes: SharedPreferences? =null
    internal var sugestions: MutableList<SearchItem>? = null
    internal var msearchHistoryTable: SearchHistoryTable? = null
    var txtuser: TextView? = null
    var txtmail: TextView? = null
    var profileimg: CircleImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val tr = ChangeBounds()
            window.sharedElementExitTransition = tr
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferenceslikes = this@MainActivity.getSharedPreferences(PREFSNAME, 0)

        sharedPreferencesapp = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val notificationstate = sharedPreferencesapp!!.getBoolean(resources.getString(R.string.notificaçãoes), true)
        if (notificationstate) {
            FirebaseMessaging.getInstance().subscribeToTopic("todos")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("todos")
        }



        searchViewp.hint = "LuxVilla: Todas"
        searchViewp.setTextSize(18f)
        searchViewp.setVoice(true)
        searchViewp.setTextStyle(1)
        searchViewp.setCursorDrawable(R.drawable.cursor)
        searchViewp.shouldClearOnClose = true

        val nightmode = sharedPreferencesapp!!.getBoolean(resources.getString(R.string.night_mode), false)
        if (nightmode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            searchViewp.setTheme(SearchView.THEME_DARK)
            searchViewp.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.card_background))
            searchViewp.setIconColor(ContextCompat.getColor(this@MainActivity, R.color.colorsearchicons))
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            searchViewp.setTheme(SearchView.THEME_LIGHT)
            searchViewp.setIconColor(ContextCompat.getColor(this@MainActivity, R.color.colorsearchicons))
        }


        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
        adapter = BDAdapter(this)


        tabs.setupWithViewPager(vpgr)


        val numerocolunas = adapter!!.numerodecolunas()
        if (numerocolunas == 0 && !isNetworkAvailable(this)) {
            vpgr.visibility = View.GONE
            tabs.visibility = View.GONE
        } else {
            val adaptador = adaptadorpaginas(supportFragmentManager)
            vpgr.adapter = adaptador

            vpgr.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {

                    when (position) {
                        0 -> {
                            searchViewp.hint = "LuxVilla: Todas"
                            navigationview.menu.getItem(0).isChecked = true
                        }
                        1 -> {
                            searchViewp.hint = "LuxVilla: Aveiro"
                            navigationview.menu.getItem(1).isChecked = true
                        }
                        2 -> {
                            searchViewp.hint = "LuxVilla: Braga"
                            navigationview.menu.getItem(2).isChecked = true
                        }
                        3 -> {
                            searchViewp.hint = "LuxVilla: Porto"
                            navigationview.menu.getItem(3).isChecked = true
                        }
                    }
                    if (!isNetworkAvailable(this@MainActivity)) {
                        Snackbar.make(vpgr, "Sem ligação há internet", Snackbar.LENGTH_LONG).setAction("ligar") {
                            val defenicoes = Intent(Settings.ACTION_WIFI_SETTINGS)
                            startActivity(defenicoes)
                        }.setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent)).show()
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

        sugestions = ArrayList()
        msearchHistoryTable = SearchHistoryTable(this@MainActivity)
        msearchHistoryTable!!.setHistorySize(3)
        searchViewp.setOnQueryTextListener(object : com.lapism.searchview.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                sugestions!!.add(SearchItem(query))
                msearchHistoryTable!!.addItem(SearchItem(query))


                val search = Intent(this@MainActivity, searchableactivity::class.java)
                search.putExtra("query", query)
                startActivity(search)
                searchViewp.shouldClearOnClose = true
                searchViewp.close(true)
                return true
            }
        })
        val searchAdapter = SearchAdapter(this@MainActivity, sugestions)
        searchAdapter.addOnItemClickListener { view, position ->
            val textView = view.findViewById<View>(R.id.textView_item_text) as TextView
            val query = textView.text.toString()

            val search = Intent(this@MainActivity, searchableactivity::class.java)
            search.putExtra("query", query)
            startActivity(search)
            searchViewp.close(true)
        }
        searchViewp.adapter = searchAdapter
        searchViewp.setOnMenuClickListener { drawerll.openDrawer(navigationview) }

        searchViewp.setOnOpenCloseListener(object : SearchView.OnOpenCloseListener {
            override fun onClose(): Boolean {
                val id = vpgr.currentItem
                when (id) {
                    0 -> searchViewp.hint = "LuxVilla: Todas"
                    1 -> searchViewp.hint = "LuxVilla: Aveiro"
                    2 -> searchViewp.hint = "LuxVilla: Braga"
                    3 -> searchViewp.hint = "LuxVilla: Porto"
                }
                searchViewp.setTextStyle(1)
                return true
            }

            override fun onOpen(): Boolean {
                searchViewp.hint = resources.getString(R.string.app_hint)
                searchViewp.setTextStyle(0)
                return true
            }
        })

        val headerLayout = navigationview.getHeaderView(0)
        txtuser=headerLayout.findViewById(R.id.textviewusername)
        txtmail=headerLayout.findViewById(R.id.textviewusermail)
        profileimg=headerLayout.findViewById(R.id.profileimage)

        navigationview.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item_1 -> vpgr.currentItem = 0
                R.id.navigation_item_2 -> vpgr.currentItem = 1
                R.id.navigation_item_3 -> vpgr.currentItem = 2
                R.id.navigation_item_4 -> vpgr.currentItem = 3
                R.id.navigation_subheader_1 -> {
                    val it = Intent(this@MainActivity, settings::class.java)
                    startActivity(it)
                }
                R.id.navigation_subheader_profile -> startActivity(Intent(this@MainActivity, Userprofile::class.java))
                R.id.navigation_subheader_signout -> {
                    sharedPreferenceslikes!!.edit().clear().apply()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@MainActivity, Loginactivity::class.java))
                    finish()
                }
            }
            drawerll.closeDrawers()
            true
        }


        val shortcutManager: ShortcutManager?

        val shortcut: ShortcutInfo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            shortcutManager = getSystemService(ShortcutManager::class.java)

            shortcut = ShortcutInfo.Builder(this, "id1")
                    .setShortLabel(resources.getString(R.string.busca))
                    .setLongLabel(resources.getString(R.string.busca))
                    .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.ic_shortcut_search))
                    .setIntents(
                            arrayOf(Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK), Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, searchableactivity::class.java)))
                    .build()
            val shortcutInfoLink = ShortcutInfo.Builder(this, "shortcut_web")
                    .setShortLabel("website")
                    .setLongLabel("LuxVilla website")
                    .setIcon(Icon.createWithResource(this, R.drawable.logo))
                    .setIntent(Intent(Intent.ACTION_VIEW, Uri.parse("http://brunoferreira.esy.es/")))
                    .build()


            shortcutManager!!.dynamicShortcuts = Arrays.asList(shortcut, shortcutInfoLink)
        }

    }


    public override fun onDestroy() {
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)
    }

    private inner class adaptadorpaginas internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {

            when (position) {
                SEPARADOR_TODAS -> return separadortodas()
                SEPARADOR_AVEIRO -> return separadoraveiro()
                SEPARADOR_BRAGA -> return separadorbraga()
                SEPARADOR_PORTO -> return separadorporto()
            }
            return null
        }

        override fun getPageTitle(position: Int): CharSequence {
            val tabsname = arrayOf("TODAS", "AVEIRO", "BRAGA", "PORTO")

            return tabsname[position]
        }

        override fun getCount(): Int {
            return 4
        }
    }

    public override fun onResume() {
        super.onResume()
        firebaseutils.getuserdata(this@MainActivity, txtuser!!, txtmail!!, profileimg!!)
        if (drawerll.isDrawerOpen(navigationview)) {
            drawerll.closeDrawer(navigationview)
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            super.recreate()
        }
    }

    override fun onBackPressed() {

        if (drawerll.isDrawerOpen(navigationview)) {
            drawerll.closeDrawer(navigationview)
        } else {
            super.onBackPressed()
        }

    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}
