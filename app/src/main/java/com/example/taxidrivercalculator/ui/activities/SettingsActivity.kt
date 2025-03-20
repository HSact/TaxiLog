package com.example.taxidrivercalculator.ui.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.example.taxidrivercalculator.helpers.LocaleHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.SettingsActivityBinding
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {

    /*private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!*/
    //val but = findViewById<Button>(R.id.buttonApply)
    private lateinit var binding: SettingsActivityBinding

    private lateinit var switchRent: MaterialSwitch
    private lateinit var switchService: MaterialSwitch
    private lateinit var switchTaxes: MaterialSwitch

    private lateinit var spinnerLang: Spinner
    private lateinit var radioTheme: RadioGroup
    private lateinit var radioDefault: RadioButton
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var radioMiKm: RadioGroup
    private lateinit var radioKm: RadioButton
    private lateinit var radioMi: RadioButton
    private lateinit var textConsumption: EditText
    private lateinit var textFuelCost: EditText
    private lateinit var textRentCost: EditText
    private lateinit var textServiceCost: EditText
    private lateinit var textGoalPerMonth: EditText
    private lateinit var radioSchedule: RadioGroup
    private lateinit var radio70: RadioButton
    private lateinit var radio61: RadioButton
    private lateinit var radio52: RadioButton
    private lateinit var textTaxRate: EditText
    private lateinit var buttonApply: Button

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

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("IS_VISIBLE_RENT", true)) {
                binding.TableRent.visibility = View.VISIBLE
            } else {
                binding.TableRent.visibility = View.GONE
            }

            if (savedInstanceState.getBoolean("IS_VISIBLE_SERVICE", true)) {
                binding.TableService.visibility = View.VISIBLE
            } else {
                binding.TableService.visibility = View.GONE
            }

            if (savedInstanceState.getBoolean("IS_VISIBLE_TAXES", true)) {
                binding.TableTaxes.visibility = View.VISIBLE
            } else {
                binding.TableTaxes.visibility = View.GONE
            }
        }

        switchRent.setOnClickListener {switchVisualize(switchRent)}
        switchService.setOnClickListener {switchVisualize(switchService)}
        switchTaxes.setOnClickListener {switchVisualize(switchTaxes)}

        buttonApply.setOnClickListener{
            applySettings()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }
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
            }
            radioLight.isChecked -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

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

    private fun switchVisualize(switch: MaterialSwitch)
    {
        val table: TableRow
        when (switch) {
            binding.switchRent -> {table = binding.TableRent}
            binding.switchService -> {table = binding.TableService}
            binding.switchTaxes -> {table = binding.TableTaxes}
            else -> return
        }
        //table.isVisible = switch.isChecked
        animateHeightChange(table)
    }

    private fun animateHeightChange(view: View) {
        val parentLayout = view.parent as ViewGroup
        val visibility = if (view.isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(parentLayout)
        view.visibility = visibility
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_VISIBLE_RENT", binding.TableRent.visibility == View.VISIBLE)
        outState.putBoolean("IS_VISIBLE_SERVICE", binding.TableService.visibility == View.VISIBLE)
        outState.putBoolean("IS_VISIBLE_TAXES", binding.TableTaxes.visibility == View.VISIBLE)
    }

    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    /*override fun onPause() {
        super.onPause()
        saveSettings()
    }*/
}