package com.hsact.taxilog.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.ui.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    val uiState by viewModel.uiState.collectAsState()
                    val user by viewModel.user.collectAsState()

                    SettingsScreen(
                        uiState = uiState,
                        user = user,
                        onSignOutClick = { logout() },
                        onSignInClick = { login() },
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        onApplyClick = { handleApplyClick() }
                    )
                }
            }
        }
    }

    private fun login() {
        viewModel.setAuthSkipped(false)
        lifecycleScope.launch {
            val result = googleAuthClient.signInAndAuthenticate(requireActivity())
            result.onFailure {
                showRetryDialog()
            }
        }
    }

    private fun logout() {
        viewModel.signOut()
    }

    private fun showRetryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.authentication_failed))
            .setMessage(getString(R.string.retry_login_question))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                login()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .show()
    }

    private fun handleApplyClick() {
        val uiState = viewModel.uiState.value
        if (uiState is SettingsUiState.Success) {
            val settings = uiState.settings

            viewModel.localeHelper.setLocale(settings.language)
            viewModel.saveSettings()
            switchTheme(settings.theme ?: "")
            
            findNavController().navigateUp()
        }
    }

    private fun switchTheme(selectedTheme: String) {
        val mode = when (selectedTheme) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}