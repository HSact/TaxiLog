package com.hsact.taxilog.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.hsact.taxilog.databinding.ActivityStartUpBinding
import com.hsact.taxilog.helpers.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class StartUpActivity : AppCompatActivity() {
    @Inject
    lateinit var settings: SettingsRepository

    private lateinit var binding: ActivityStartUpBinding
    private val logoDuration: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
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
        val theme: String = settings.theme
        if (theme == "dark") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if (theme == "light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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

    override fun onRestart() {
        super.onRestart()
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun setUp() {
        binding.SetUpLayout.alpha = 0f
        binding.LogoLayout.isVisible = false
        binding.SetUpLayout.animate().setDuration(logoDuration).alpha(1f)
    }
}