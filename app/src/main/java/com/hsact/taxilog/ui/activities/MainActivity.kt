package com.hsact.taxilog.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.ActivityMainBinding
import com.hsact.taxilog.helpers.LocaleHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var botNav: BottomNavigationView
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        botNav = navView
        loadSettings()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_goals, R.id.navigation_stats
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_log -> {
                val logIntent = Intent(this, LogActivity::class.java)
                startActivity(logIntent)
                true
            }

            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }

            R.id.action_about -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun attachBaseContext(newBase: Context) {
        var newLocale = LocaleHelper.getSavedLanguage(newBase)
        if (newLocale == "") {
            newLocale = LocaleHelper.getDefault()
        }
        super.attachBaseContext(
            LocaleHelper.updateLocale(
                newBase,
                newLocale
            )
        )
    }

    private fun loadSettings() {
        LocaleHelper.setLocale(this, LocaleHelper.getSavedLanguage(this))
    }
}