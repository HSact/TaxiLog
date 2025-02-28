package com.example.taxidrivercalculator.ui.activities

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
import com.example.taxidrivercalculator.helpers.LocaleHelper
import com.example.taxidrivercalculator.R


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
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
        super.attachBaseContext(
            LocaleHelper.updateLocale(
                newBase,
                LocaleHelper.getSavedLanguage(newBase)
            )
        )
    }

    /*override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.closeButton ->{

                val text = findViewById<TextView>(R.id.textAbout)
                text.text = "xyu"


                val homeIntent = Intent (this,MainActivity::class.java)
                startActivity(homeIntent)
            }
        }
        return super.onContextItemSelected(item)
    }*/


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}