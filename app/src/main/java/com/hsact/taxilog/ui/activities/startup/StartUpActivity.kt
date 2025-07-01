package com.hsact.taxilog.ui.activities.startup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.hsact.taxilog.databinding.ActivityStartUpBinding
import com.hsact.taxilog.domain.model.settings.UserSettings
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.activities.settings.SettingsActivity
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class StartUpActivity : AppCompatActivity() {

    private val viewModel: StartUpViewModel by viewModels()
    private lateinit var settings: UserSettings

    private lateinit var binding: ActivityStartUpBinding
    private val logoDuration: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        viewModel.settings.observe(this) { settings ->
            settings?.let { s ->
                this.settings = s
                val theme: String = s.theme ?: getCurrentTheme()
                setTheme(theme)
                binding.imageLogo.alpha = 0f
                binding.buttonOkay.setOnClickListener {
                    startActivity(
                        Intent(
                            this,
                            SettingsActivity::class.java
                        )
                    )
                }
                binding.buttonNope.setOnClickListener {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        )
                    )
                }

                binding.imageLogo.animate().setDuration(logoDuration).alpha(1f)
                if (settings.isConfigured) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                    }, logoDuration)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        setUp()
                    }, logoDuration)
                }
            }
        }
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper.wrapContext(newBase))
    }

    private fun setTheme(theme: String) {
        if (theme == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if (theme == "light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onRestart() {
        super.onRestart()
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun getCurrentTheme(): String {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        if (currentTheme == 1) {
            return "light"
        }
        if (currentTheme == 2) {
            return "dark"
        }
        return "default"
    }

    private fun setUp() {
        binding.SetUpLayout.alpha = 0f
        binding.LogoLayout.isVisible = false
        binding.SetUpLayout.animate().setDuration(logoDuration).alpha(1f)
    }
}