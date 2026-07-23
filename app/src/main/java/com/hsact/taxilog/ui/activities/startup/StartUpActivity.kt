@file:Suppress("DEPRECATION")

package com.hsact.taxilog.ui.activities.startup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hsact.domain.model.settings.UserSettings
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.databinding.ActivityStartUpBinding
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.activities.settings.SettingsActivity
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartUpActivity : AppCompatActivity() {
    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val viewModel: StartUpViewModel by viewModels()
    private lateinit var settings: UserSettings
    private var isInitialized = false

    private lateinit var binding: ActivityStartUpBinding
    private val logoDuration: Long = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        lifecycleScope.launch {
            // Wait for both settings to be loaded and AuthState to be determined (not Loading)
            val initializationFlow = combine(
                viewModel.settings.filterNotNull(),
                viewModel.authState.filter { it !is AuthState.Loading }
            ) { settings, authState ->
                Pair(settings, authState)
            }

            val (settings, authState) = initializationFlow.first()
            
            if (isInitialized) return@launch
            isInitialized = true

            this@StartUpActivity.settings = settings
            val theme: String = settings.theme ?: getCurrentTheme()
            setTheme(theme)

            binding.imageLogo.alpha = 0f
            binding.buttonOkay.setOnClickListener {
                val intent = Intent(this@StartUpActivity, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            binding.buttonNope.setOnClickListener {
                val intent = Intent(this@StartUpActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

            binding.imageLogo.animate().setDuration(logoDuration).alpha(1f)

            when (authState) {
                is AuthState.Authenticated -> {
                    // 1. User is authenticated in Firebase → proceed
                    proceedAfterLogin()
                }
                is AuthState.NotAuthenticated -> {
                    if (!viewModel.isAuthSkipped()) {
                        // 2. Not authenticated → begin login process
                        showAuthChoiceDialog()
                    } else {
                        proceedAfterLogin()
                    }
                }
                AuthState.Loading -> {
                    // This case is filtered out by initializationFlow
                }
            }
        }
    }

    private fun showAuthChoiceDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.sign_in_title))
            .setMessage(getString(R.string.sign_in_description))
            .setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                lifecycleScope.launch {
                    delay(logoDuration - 100)
                    signInWithGoogle()
                }
            }
            .setNegativeButton(getString(R.string.skip)) { _, _ ->
                viewModel.setAuthSkipped(true)
                proceedAfterLogin()
            }
            .setCancelable(false)
            .show()
    }

    private fun proceedAfterLogin() {
        if (settings.isConfigured) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }, logoDuration)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                setUp()
            }, logoDuration)
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

    private fun signInWithGoogle() {
        lifecycleScope.launch {
            val result = googleAuthClient.signInAndAuthenticate(this@StartUpActivity)
            result.onSuccess { user ->
                Log.d(
                    "GoogleSignIn",
                    "signInWithGoogle:success. Email: ${user.email}"
                )
                proceedAfterLogin()
            }.onFailure { e ->
                Log.w("GoogleSignIn", "CredentialManager sign in failed", e)
                showRetryDialog()
            }
        }
    }

    private fun showRetryDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.authentication_failed))
            .setMessage(getString(R.string.retry_login_question))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                signInWithGoogle()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
            }
            .show()
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