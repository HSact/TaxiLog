package com.hsact.taxilog.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.hsact.taxilog.helpers.LocaleHelper
import com.hsact.taxilog.R
import com.hsact.taxilog.helpers.ContextWrapper
import com.hsact.taxilog.helpers.LocaleProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity @Inject constructor(): AppCompatActivity() {
    @Inject
    lateinit var localeHelper: LocaleHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val textAbout = findViewById<TextView>(R.id.textAbout)
        val spannableString = SpannableString(getString(R.string.app_repository))

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.https_github_com_hsact_taxilog)))
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textAbout.text = spannableString
        textAbout.movementMethod = LinkMovementMethod.getInstance()
        textAbout.highlightColor = Color.TRANSPARENT
    }
    override fun attachBaseContext(newBase: Context) {
        /*super.attachBaseContext(
            localeHelper.updateLocale(
                newBase,
                localeHelper.getSavedLanguage()
            )
        )*/
        super.attachBaseContext(ContextWrapper.wrapContext(newBase, Locale.getDefault().language))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}