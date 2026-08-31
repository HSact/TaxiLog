package com.hsact.taxilog.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.components.FeedbackDialog
import com.hsact.taxilog.ui.fragments.feedback.FeedbackViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity
    @Inject
    constructor() : AppCompatActivity() {
        private val feedbackViewModel: FeedbackViewModel by viewModels()
        private var showFeedbackDialog by mutableStateOf(false)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_about)
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.title = getString(R.string.title_about)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            setupFeedbackObserver()

            findViewById<ComposeView>(R.id.compose_view_about).setContent {
                AppTheme {
                    AboutScreenContent()

                    if (showFeedbackDialog) {
                        val isSending by feedbackViewModel.isSending.collectAsState()
                        FeedbackDialog(
                            onDismiss = { if (!isSending) showFeedbackDialog = false },
                            onSend = { message ->
                                feedbackViewModel.sendFeedback(message)
                            },
                            isSending = isSending
                        )
                    }
                }
            }
        }

        private fun setupFeedbackObserver() {
            feedbackViewModel.feedbackResult
                .onEach { result ->
                    val message = when (result) {
                        FeedbackViewModel.FeedbackResult.Success -> {
                            showFeedbackDialog = false
                            getString(R.string.feedback_success)
                        }
                        FeedbackViewModel.FeedbackResult.Error -> getString(R.string.feedback_error)
                        FeedbackViewModel.FeedbackResult.Cooldown -> getString(R.string.feedback_cooldown)
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
                .launchIn(lifecycleScope)
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_main, menu)
            // Hide "About" item since we are already in AboutActivity
            menu.findItem(R.id.action_about)?.isVisible = false
            // Hide "Settings" if not needed, but let's keep it if user wants to navigate
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_report_bug -> {
                    showFeedbackDialog = true
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

        override fun onSupportNavigateUp(): Boolean {
            onBackPressedDispatcher.onBackPressed()
            return super.onSupportNavigateUp()
        }
    }
