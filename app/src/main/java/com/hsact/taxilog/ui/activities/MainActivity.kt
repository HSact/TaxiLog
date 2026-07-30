package com.hsact.taxilog.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isBottomNavVisible =
                when (destination.id) {
                    R.id.shiftForm, R.id.shiftDetailFragment, R.id.settingsFragment -> false
                    else -> true
                }
            setBottomNavVisible(isBottomNavVisible)
            invalidateOptionsMenu()
        }

        val appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_log,
                    R.id.navigation_goals,
                    R.id.navigation_stats,
                ),
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            val options =
                NavOptions
                    .Builder()
                    .setEnterAnim(R.anim.fade_in)
                    .setExitAnim(R.anim.fade_out)
                    .setPopEnterAnim(R.anim.fade_in)
                    .setPopExitAnim(R.anim.fade_out)
                    .setLaunchSingleTop(true)
                    .setPopUpTo(navController.graph.startDestinationId, false, saveState = true)
                    .setRestoreState(true)
                    .build()

            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId, null, options)
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.setPadding(0, 0, 0, navBarHeight)
            insets
        }

        if (savedInstanceState == null && intent.getBooleanExtra("NAVIGATE_TO_SETTINGS", false)) {
            navController.navigate(R.id.settingsFragment, null, getSlideNavOptions())
        }
    }

    private fun getSlideNavOptions(): NavOptions =
        NavOptions
            .Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestination = navController.currentDestination?.id
        return if (currentDestination == R.id.shiftDetailFragment || currentDestination == R.id.settingsFragment) {
            false
        } else {
            menuInflater.inflate(R.menu.menu_main, menu)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment_activity_main).navigate(
                    R.id.settingsFragment,
                    null,
                    getSlideNavOptions(),
                )
                true
            }

            R.id.action_about -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    /**
     * Animates the BottomNavigationView visibility.
     * Slides down to hide, slides up to show.
     */
    private fun setBottomNavVisible(visible: Boolean) {
        val navView: BottomNavigationView = binding.navView
        val duration = resources.getInteger(R.integer.anim_duration_short).toLong()

        if (visible) {
            navView
                .animate()
                .translationY(0f)
                .setDuration(duration)
                .withStartAction { navView.isVisible = true }
                .start()
        } else {
            // Handle case where height is not yet measured (e.g. rapid navigation on startup)
            val height = if (navView.height > 0) navView.height.toFloat() else 200f
            navView
                .animate()
                .translationY(height)
                .setDuration(duration)
                .withEndAction { navView.isVisible = false }
                .start()
        }
    }
}
