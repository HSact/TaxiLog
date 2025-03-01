package com.example.taxidrivercalculator.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.example.taxidrivercalculator.helpers.LocaleHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.SettingsActivityBinding


class SettingsActivity : AppCompatActivity() {

    /*private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!*/
    //val but = findViewById<Button>(R.id.buttonApply)
    private lateinit var binding: SettingsActivityBinding

    lateinit var switchRent: Switch
    lateinit var switchService: Switch
    lateinit var switchTaxes: Switch

    lateinit var spinnerLang: Spinner
    lateinit var radioTheme: RadioGroup
    lateinit var radioDefault: RadioButton
    lateinit var radioLight: RadioButton
    lateinit var radioDark: RadioButton
    lateinit var radioMiKm: RadioGroup
    lateinit var radioKm: RadioButton
    lateinit var radioMi: RadioButton
    lateinit var textConsumption: EditText
    lateinit var textFuelCost: EditText
    lateinit var textRentCost: EditText
    lateinit var textServiceCost: EditText
    lateinit var textGoalPerMonth: EditText
    lateinit var radioSchedule: RadioGroup
    lateinit var radio70: RadioButton
    lateinit var radio61: RadioButton
    lateinit var radio52: RadioButton
    lateinit var textTaxRate: EditText
    lateinit var buttonApply: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleHelper.setLocale(this, LocaleHelper.getSavedLanguage(this))


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
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
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
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.updateLocale(
                newBase,
                LocaleHelper.getSavedLanguage(newBase)
            )
        )
    }

    private fun bindItems() {
        switchRent = binding.switchRent
        switchService = binding.switchService
        switchTaxes = binding.switchTaxes

        spinnerLang = binding.spinnerLang

        radioTheme = binding.radioTheme
        radioDefault = binding.radioDefault
        radioLight = binding.radioLight
        radioDark = binding.radioDark

        radioMiKm = binding.radioMiKm
        radioKm = binding.radioKm
        radioMi = binding.radioMi

        textConsumption = binding.editTextConsumption

        textFuelCost = binding.editTextFuelPrice
        textRentCost = binding.editTextRentCost
        textServiceCost = binding.editTextServiceCost
        textGoalPerMonth = binding.editTextGoalPerMonth

        radioSchedule = binding.radioSchedule
        radio70 = binding.radio70
        radio61 = binding.radio61
        radio52 = binding.radio52

        textTaxRate = binding.editTextTaxRate
        buttonApply = binding.buttonApply
    }
    private fun applySettings()
    {
        //setLocate(injectLangSpinner())
        LocaleHelper.setLocale(this, injectLangSpinner())
        saveSettings()
        switchTheme()
    }
    private fun loadSettings()
    {
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
        textFuelCost.setText(settings.getString("Fuel_price", ""))
        switchService.isChecked = settings.getBoolean("Service", false)
        textServiceCost.setText(settings.getString("Service_cost", ""))
        textGoalPerMonth.setText(settings.getString("Goal_per_month", ""))
        radioSchedule.check(settings.getInt("Schedule", 0))
        switchTaxes.isChecked = settings.getBoolean("Taxes", false)
        textTaxRate.setText(settings.getString("Tax_rate", ""))
    }
    private fun loadLangSpinner()
    {
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
    private fun loadThemeSelection()
    {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        if (currentTheme==-100)
        {
            radioDefault.isChecked=true
            radioLight.isChecked=false
            radioDark.isChecked=false
        }

        if (currentTheme==1)
        {
            radioDefault.isChecked=false
            radioLight.isChecked=true
            radioDark.isChecked=false
        }
        if (currentTheme==2)
        {
            radioDefault.isChecked=false
            radioLight.isChecked=false
            radioDark.isChecked=true
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
    private fun switchTheme()
    {
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
        }
        /*val settings = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        settings.putString("Theme", getSelectedTheme())
        settings.apply()*/
    }
    /*private fun setLocate(lang: String)
    {
        if (lang != "en" && lang != "ru")
        {
            return
        }
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
        startActivity(Intent (this, MainActivity::class.java))
    }*/

    /*fun loadLocate() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return

        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language: String = sharedPreferences.getString("My_Lang", "").toString()
        setLocate(language)
    }*/

    private fun getLocate(): String
    {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("My_Lang", "").toString()
    }
    private fun getSelectedTheme(): String
    {
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
    private fun getKmMi(): Boolean
    {
        return radioKm.isChecked
    }


    private fun saveSettings()
    {
        val settings = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        settings.putBoolean("Seted_up", true)
        settings.putString("My_Lang", injectLangSpinner())
        settings.putString("Theme", getSelectedTheme())
        settings.putBoolean("KmMi", getKmMi())
        settings.putString("Consumption", textConsumption.text.toString())
        settings.putString("Fuel_price", textFuelCost.text.toString())
        settings.putBoolean("Rented", switchRent.isChecked)
        settings.putString("Rent_cost", textRentCost.text.toString())
        settings.putBoolean("Service", switchService.isChecked)
        settings.putString("Service_cost", textServiceCost.text.toString())
        settings.putString("Goal_per_month", textGoalPerMonth.text.toString())
        settings.putInt("Schedule", radioSchedule.checkedRadioButtonId)
        settings.putBoolean("Taxes", switchTaxes.isChecked)
        settings.putString("Tax_rate", textTaxRate.text.toString())

        val radioScheduleText = when (radioSchedule.checkedRadioButtonId) {
            R.id.radio70 -> radio70.text
            R.id.radio61 -> radio61.text
            R.id.radio52 -> radio52.text
            else -> {"0"}
        }
        settings.putString("Schedule_text", radioScheduleText.toString())
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

    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /*override fun onPause() {
        super.onPause()
        saveSettings()
    }*/
}