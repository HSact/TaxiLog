package com.example.taxidrivercalculator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taxidrivercalculator.databinding.SettingsActivityBinding
import java.util.*


class SettingsActivity : AppCompatActivity() {

    /*private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!*/
    //val but = findViewById<Button>(R.id.buttonApply)
    private lateinit var binding: SettingsActivityBinding

    companion object
    {

    }

    lateinit var switchRent: Switch
    lateinit var switchService: Switch
    lateinit var switchTaxes: Switch

    lateinit var spinnerLang: Spinner
    lateinit var radioTheme: RadioGroup
    lateinit var radioLight: RadioButton
    lateinit var radioDark: RadioButton
    lateinit var radioMiKm: RadioGroup
    lateinit var radioKm: RadioButton
    lateinit var radioMi: RadioButton
    lateinit var textConsumption: EditText
    lateinit var textRentCost: EditText
    lateinit var textServiceCost: EditText
    lateinit var textGoalPerMonth: EditText
    lateinit var radioSchedule: RadioGroup
    lateinit var textTaxRate: EditText
    lateinit var buttonApply: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)



        supportActionBar?.title = getString(R.string.title_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindItems()
        loadSettings()
        loadThemeSelection()
        switchVisualize(switchRent)
        switchVisualize(switchService)
        switchVisualize(switchTaxes)

        switchRent.setOnClickListener {switchVisualize(switchRent)}
        switchService.setOnClickListener {switchVisualize(switchService)}
        switchTaxes.setOnClickListener {switchVisualize(switchTaxes)}

        //radioDark.setOnClickListener {switchTheme()}
        //radioLight.setOnClickListener {switchTheme()}

        buttonApply.setOnClickListener{
            applySettings()
            recreate()
        }


        /*if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }*/



        /*but.setOnClickListener{
            val homeIntent = Intent(this, HomeFragment::class.java)
            startActivity(homeIntent)
        }*/

    }



    private fun bindItems() {
        switchRent = binding.switchRent
        switchService = binding.switchService
        switchTaxes = binding.switchTaxes

        spinnerLang = binding.spinnerLang

        radioTheme = binding.radioTheme
        radioLight = binding.radioLight
        radioDark = binding.radioDark

        radioMiKm = binding.radioMiKm
        radioKm = binding.radioKm
        radioMi = binding.radioMi

        textConsumption = binding.editTextConsumption
        textRentCost = binding.editTextRentCost
        textServiceCost = binding.editTextServiceCost
        textGoalPerMonth = binding.editTextGoalPerMonth
        radioSchedule = binding.radioSchedule
        textTaxRate = binding.editTextTaxRate
        buttonApply = binding.buttonApply
    }



    private fun applySettings() {
        setLocate(injectLangSpinner())
        saveSettings()
        switchTheme()

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) <== work
    }
    private fun loadSettings() {
        loadLangSpinner()
        val settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        if (!(settings.getBoolean("Seted_up", false)))
        {
            return
        }
        loadThemeSelection()
        loadKmMiSelection()
        textConsumption.setText(settings.getString("Consumption", ""))
        switchRent.isChecked = settings.getBoolean("Rented", false)
        textRentCost.setText(settings.getString("Rent_cost", ""))
        switchService.isChecked = settings.getBoolean("Service", false)
        textServiceCost.setText(settings.getString("Service_cost", ""))
        textGoalPerMonth.setText(settings.getString("Goal_per_month", ""))
        radioSchedule.check(settings.getInt("Schedule", 0))
        switchTaxes.isChecked = settings.getBoolean("Taxes", false)
        textTaxRate.setText(settings.getString("Tax_rate", ""))



    }
    private fun loadLangSpinner() {
        val currentLang: String = getLocate()
        if (currentLang=="en")
        {
            spinnerLang.setSelection(0)
        }
        if (currentLang=="ru")
        {
            spinnerLang.setSelection(1)
        }
    }
    private fun loadThemeSelection() {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        if (currentTheme==2)
        {
            radioLight.isChecked=false
            radioDark.isChecked=true
        }
        if (currentTheme==1)
        {
            radioLight.isChecked=true
            radioDark.isChecked=false
        }
    }
    private fun loadKmMiSelection()
    {
        val settings = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val kmMi: Boolean = settings.getBoolean("KmMi", false)
        if (kmMi)
        {
            radioKm.isChecked=true
            radioMi.isChecked=false
        }
        else
        {
            radioKm.isChecked=false
            radioMi.isChecked=true
        }
    }


    private fun switchTheme() {

        when {
            radioDark.isChecked -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //return
                //var a = AppCompatDelegate.getDefaultNightMode()
            }
            radioLight.isChecked -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //return
            }

            /*else -> {
                return
            }*/
        }
        /*val settings = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        settings.putString("Theme", getSelectedTheme())
        settings.apply()*/
    }

    private fun setLocate(lang: String) {
        if (lang != "en" && lang != "ru") {
            return
        }
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()

        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        /*baseContext.applicationContext.createConfigurationContext(configuration)
        baseContext.resources.displayMetrics.setTo(metrics)
        val overrideConfiguration = baseContext.resources.configuration
        overrideConfiguration.setLocales(LocaleList)
        val context = createConfigurationContext(overrideConfiguration)
        val resources: Resources = context.resources*/
        //Locale.setDefault(locale)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()


        startActivity(Intent (this, MainActivity::class.java))
    }

    /*fun loadLocate() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return

        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language: String = sharedPreferences.getString("My_Lang", "").toString()
        setLocate(language)
    }*/

    private fun getLocate(): String {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", "").toString()
    }
    private fun getSelectedTheme(): String {
        if (radioDark.isChecked)
        {
            return "dark"
        }
        if (radioLight.isChecked)
        {
            return "light"
        }
        return ""
    }
    private fun getKmMi(): Boolean {
        if (radioKm.isChecked)
        {
            return true
        }
        return false
    }


    private fun saveSettings()
    {
        val settings = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        settings.putBoolean("Seted_up", true)
        settings.putString("My_Lang", injectLangSpinner())
        settings.putString("Theme", getSelectedTheme())
        settings.putBoolean("KmMi", getKmMi())
        settings.putString("Consumption", textConsumption.text.toString())
        settings.putBoolean("Rented", switchRent.isChecked)
        settings.putString("Rent_cost", textRentCost.text.toString())
        settings.putBoolean("Service", switchService.isChecked)
        settings.putString("Service_cost", textServiceCost.text.toString())
        settings.putString("Goal_per_month", textGoalPerMonth.text.toString())
        settings.putInt("Schedule", radioSchedule.checkedRadioButtonId)
        settings.putBoolean("Taxes", switchTaxes.isChecked)
        settings.putString("Tax_rate", textTaxRate.text.toString())
        settings.apply()
    }


    private fun switchVisualize(switch: Switch)
    {
        val table: TableRow
        when (switch) {
            binding.switchRent -> {table = binding.TableRent}
            binding.switchService -> {table = binding.TableService}
            binding.switchTaxes -> {table = binding.TableTaxes}
            else -> return
        }
        table.isVisible = switch.isChecked

    }

    private fun injectLangSpinner(): String
    {
        when (spinnerLang.selectedItemPosition)
        {
            0 -> return "en"
            1 -> return "ru"
        }
           return ""
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        //supportFragmentManager.putFragment(outState,"Settings", SettingsActivity)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return super.onSupportNavigateUp()
    }

    /*override fun onPause() {
        super.onPause()
        saveSettings()
    }*/
}