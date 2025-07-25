@file:Suppress("DEPRECATION")

package com.hsact.taxilog.ui.activities.startup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hsact.domain.model.settings.UserSettings
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.auth.GoogleAuthResult
import com.hsact.taxilog.databinding.ActivityStartUpBinding
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.activities.settings.SettingsActivity
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartUpActivity : AppCompatActivity() {
    @Inject
    lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    private companion object {
        const val RC_SIGN_IN = 9001
    }

    private val viewModel: StartUpViewModel by viewModels()
    private lateinit var settings: UserSettings

    private lateinit var binding: ActivityStartUpBinding
    private val logoDuration: Long = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                googleAuthClient.handleResult(result.data) { authResult ->
                    when (authResult) {
                        is GoogleAuthResult.Success -> {
                            proceedAfterLogin()
                        }
                        is GoogleAuthResult.Error -> {
                            showRetryDialog()
                        }
                    }
                }
            } else {
                viewModel.setAuthSkipped(true)
                proceedAfterLogin()
            }
        }

        viewModel.settings.observe(this) { settings ->
            settings?.let { s ->
                this.settings = s
                val theme: String = s.theme ?: getCurrentTheme()
                setTheme(theme)
                binding.imageLogo.alpha = 0f
                binding.buttonOkay.setOnClickListener {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                binding.buttonNope.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                binding.imageLogo.animate().setDuration(logoDuration).alpha(1f)

                val firebaseAuth = FirebaseAuth.getInstance()
                val googleAccount = GoogleSignIn.getLastSignedInAccount(this)

                if (firebaseAuth.currentUser != null) {
                    // 1. User is authenticated in Firebase → proceed
                    proceedAfterLogin()
                } else if (googleAccount != null) {
                    // 2. Authenticated in Google, but not in Firebase → request Firebase
                    firebaseAuthWithGoogle(googleAccount.idToken!!)
                } else if (!viewModel.isAuthSkipped()) {
                    // 3. Not authenticated → begin login process
                    showAuthChoiceDialog()
                }
                 else {
                    proceedAfterLogin()
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
                    signInLauncher.launch(googleAuthClient.getSignInIntent())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            @Suppress("DEPRECATION")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d("GoogleSignIn", "signInWithCredential:success. Email: ${user?.email}")
                    proceedAfterLogin()
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,
                        getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
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
                retryGoogleSignIn()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                finish()
            }
            .show()
    }

    private fun retryGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        Handler(Looper.getMainLooper()).postDelayed({
            signInLauncher.launch(googleSignInClient.signInIntent)
        }, 500)
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