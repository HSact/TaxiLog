package com.example.taxidrivercalculator.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.example.taxidrivercalculator.databinding.ActivityStartUpBinding
import com.example.taxidrivercalculator.helpers.SettingsHelper
import kotlin.system.exitProcess

class StartUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartUpBinding
    private val logoDuration: Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)
        binding.imageLogo.alpha = 0f
        binding.textLogo.alpha = 0f
        binding.buttonOkay.setOnClickListener{startActivity(Intent(this, SettingsActivity::class.java))}
        binding.buttonNope.setOnClickListener{startActivity(Intent(this, MainActivity::class.java))}
        val settings = SettingsHelper.getInstance(this)
        //val settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val theme: String = settings.theme
        //val theme: String = settings.getString("Theme", "").toString()
        if (theme=="dark")
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            //var a = AppCompatDelegate.getDefaultNightMode()
        }
        if (theme=="light")
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            //var a = AppCompatDelegate.getDefaultNightMode()
        }
        binding.imageLogo.animate().setDuration(logoDuration).alpha(1f)
        binding.textLogo.animate().setDuration(logoDuration).alpha(1f)
        //if (settings.getBoolean("Seted_up", false))
        if(settings.seted_up)
        {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent (this, MainActivity::class.java))}, logoDuration)
        }
        else
        {
            Handler(Looper.getMainLooper()).postDelayed({
                setUp()}, logoDuration)
        }
    }

    override fun onRestart() {
        super.onRestart()
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun setUp()
    {
        //binding.textFirstLaunch.alpha = 0f
        //binding.buttonOkay.alpha = 0f
        binding.SetUpLayout.alpha = 0f

        binding.LogoLayout.isVisible=false

        binding.SetUpLayout.animate().setDuration(logoDuration).alpha(1f)
        //binding.textFirstLaunch.animate().setDuration(logo_duration).alpha(1f)
        //binding.buttonOkay.animate().setDuration(logo_duration).alpha(1f)

    }
}