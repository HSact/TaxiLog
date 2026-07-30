package com.hsact.taxilog.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity
    @Inject
    constructor() : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_about)
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.title = getString(R.string.title_about)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            findViewById<ComposeView>(R.id.compose_view_about).setContent {
                AppTheme {
                    AboutScreenContent()
                }
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            onBackPressedDispatcher.onBackPressed()
            return super.onSupportNavigateUp()
        }
    }
