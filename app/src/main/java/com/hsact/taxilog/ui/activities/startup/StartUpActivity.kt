package com.hsact.taxilog.ui.activities.startup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hsact.domain.model.settings.UserSettings
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.databinding.ActivityStartUpBinding
import com.hsact.taxilog.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartUpActivity : AppCompatActivity() {

    companion object {
        private const val LOGO_DURATION: Long = 1200
    }
    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val viewModel: StartUpViewModel by viewModels()
    private lateinit var settings: UserSettings
    private var isInitialized = false
    private var isNavigating = false
    private var isAuthInProgress = false
    private var currentDialog: Dialog? = null

    private lateinit var binding: ActivityStartUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        lifecycleScope.launch {
            // Wait for both settings to be loaded and AuthState to be determined (not Loading)
            val initializationFlow =
                combine(
                    viewModel.settings.filterNotNull(),
                    viewModel.authState.filter { it !is AuthState.Loading },
                ) { settings, authState ->
                    Pair(settings, authState)
                }

            initializationFlow.collect { (settings, authState) ->
                handleInitialization(settings, authState)
            }
        }

        // Fallback: If nothing happens in 5 seconds, try to proceed with what we have
        lifecycleScope.launch {
            delay(5000)
            if (!isInitialized) {
                Log.w("StartUpActivity", "Initialization timed out, forcing setup")
                val fallbackSettings = viewModel.settings.value ?: UserSettings.default
                val fallbackAuthState = viewModel.authState.value
                handleInitialization(fallbackSettings, fallbackAuthState)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check state when returning to the app (e.g., from settings)
        // Skip if we are currently in the middle of a Google Sign-In process
        if (isInitialized && !isNavigating && !isAuthInProgress) {
            val currentSettings = viewModel.settings.value
            val currentAuthState = viewModel.authState.value
            if (currentSettings != null && currentAuthState !is AuthState.Loading) {
                handleInitialization(currentSettings, currentAuthState)
            }
        }
    }

    private fun handleInitialization(
        settings: UserSettings,
        authState: AuthState,
    ) {
        if (isNavigating) return

        this.settings = settings

        if (!isInitialized) {
            isInitialized = true
            applyInitialSetup(settings)
        }

        when (authState) {
            is AuthState.Authenticated -> {
                currentDialog?.dismiss()
                proceedAfterLogin()
            }
            is AuthState.NotAuthenticated -> {
                if (!viewModel.isAuthSkipped()) {
                    showAuthChoiceDialog()
                } else {
                    proceedAfterLogin()
                }
            }
            AuthState.Loading -> {
                // Wait for collect to emit a non-loading state
            }
        }
    }

    private fun applyInitialSetup(settings: UserSettings) {
        val theme: String = settings.theme ?: getCurrentTheme()
        setTheme(theme)

        binding.imageLogo.alpha = 0f
        binding.buttonOkay.setOnClickListener {
            navigateToMain(true)
        }

        binding.buttonNope.setOnClickListener {
            navigateToMain(false)
        }

        binding.imageLogo
            .animate()
            .setDuration(LOGO_DURATION)
            .alpha(1f)
    }

    private fun navigateToMain(toSettings: Boolean) {
        if (isNavigating) return
        isNavigating = true

        val intent = Intent(this, MainActivity::class.java)
        if (toSettings) intent.putExtra("NAVIGATE_TO_SETTINGS", true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun showAuthChoiceDialog() {
        if (currentDialog?.isShowing == true || isNavigating) return

        currentDialog =
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.sign_in_title))
                .setMessage(getString(R.string.sign_in_description))
                .setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                    lifecycleScope.launch {
                        delay(LOGO_DURATION - 100)
                        signInWithGoogle()
                    }
                }.setNegativeButton(getString(R.string.skip)) { _, _ ->
                    viewModel.setAuthSkipped(true)
                    // proceedAfterLogin will be called via collect when settings update or next emit
                    proceedAfterLogin()
                }.setCancelable(false)
                .show()
    }

    private fun proceedAfterLogin() {
        if (isNavigating) return

        if (settings.isConfigured) {
            isNavigating = true
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }, LOGO_DURATION)
        } else {
            setUp()
        }
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
            isAuthInProgress = true
            try {
                val result = googleAuthClient.signInAndAuthenticate(this@StartUpActivity)
                result
                    .onSuccess { user ->
                        Log.d(
                            "GoogleSignIn",
                            "signInWithGoogle:success. Email: ${user.email}",
                        )
                        // Navigation will happen via initializationFlow.collect
                    }.onFailure { e ->
                        Log.w("GoogleSignIn", "CredentialManager sign in failed", e)
                        if (e is NoCredentialException) {
                            showNoAccountDialog()
                        } else {
                            showRetryDialog()
                        }
                    }
            } finally {
                isAuthInProgress = false
            }
        }
    }

    private fun showNoAccountDialog() {
        if (isNavigating) return
        currentDialog?.dismiss()

        currentDialog =
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.no_google_accounts))
                .setMessage(getString(R.string.add_account_instruction))
                .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                    try {
                        val intent = Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                        intent.putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("StartUpActivity", "Could not open account settings", e)
                    }
                }.setNegativeButton(getString(R.string.skip)) { _, _ ->
                    viewModel.setAuthSkipped(true)
                    proceedAfterLogin()
                }.setCancelable(false)
                .show()
    }

    private fun showRetryDialog() {
        if (isNavigating) return
        currentDialog?.dismiss()

        currentDialog =
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.authentication_failed))
                .setMessage(getString(R.string.retry_login_question))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.retry)) { _, _ ->
                    signInWithGoogle()
                }.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    finish()
                }.show()
    }

    private fun getCurrentTheme(): String {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        return when (currentTheme) {
            1 -> "light"
            2 -> "dark"
            else -> "default"
        }
    }

    private fun setUp() {
        binding.SetUpLayout.alpha = 0f
        binding.LogoLayout.isVisible = false
        binding.SetUpLayout
            .animate()
            .setDuration(LOGO_DURATION)
            .alpha(1f)
    }
}
