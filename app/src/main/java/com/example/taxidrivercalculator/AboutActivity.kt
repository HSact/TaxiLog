package com.example.taxidrivercalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView


class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.title = getString(R.string.title_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        onBackPressed();
        return super.onSupportNavigateUp()
    }
}