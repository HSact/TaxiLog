package com.hsact.taxilog.ui.activities.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TableRow
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.materialswitch.MaterialSwitch
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.SettingsActivityBinding
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity: AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

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

        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
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

        switchRent.setOnClickListener { switchVisualize(switchRent) }
        switchService.setOnClickListener { switchVisualize(switchService) }
        switchTaxes.setOnClickListener { switchVisualize(switchTaxes) }

        buttonApply.setOnClickListener {
            applySettings()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper.wrapContext(newBase))
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

    private fun applySettings() {
        viewModel.localeHelper.setLocale(this, injectLangSpinner())
        saveSettings()
        switchTheme()
    }

    private fun loadSettings() {
        val settings = viewModel.settings.value ?: return
        loadLangSpinner(settings)
        if (!(settings.isConfigured)) {
            return
        }
        loadThemeSelection()
        loadKmMiSelection(settings)
        textConsumption.setText(settings.consumption)
        switchRent.isChecked = settings.rented
        textRentCost.setText(settings.rentCost)
        textFuelCost.setText(settings.fuelPrice)
        switchService.isChecked = settings.service
        textServiceCost.setText(settings.serviceCost)
        textGoalPerMonth.setText(settings.goalPerMonth)
        radioSchedule.check(getScheduleId(settings.schedule))
        switchTaxes.isChecked = settings.taxes
        textTaxRate.setText(settings.taxRate)
    }

    private fun loadLangSpinner(settings: UserSettings) {
        var currentLang: String = settings.language ?: Locale.getDefault().language
        if (currentLang.isEmpty()) {
            currentLang = Locale.getDefault().language
        }
        if (currentLang == "en") {
            spinnerLang.setSelection(0)
        }
        if (currentLang == "ru") {
            spinnerLang.setSelection(1)
        }
    }

    private fun loadThemeSelection() {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        if (currentTheme == -100) {
            radioDefault.isChecked = true
            radioLight.isChecked = false
            radioDark.isChecked = false
        }

        if (currentTheme == 1) {
            radioDefault.isChecked = false
            radioLight.isChecked = true
            radioDark.isChecked = false
        }
        if (currentTheme == 2) {
            radioDefault.isChecked = false
            radioLight.isChecked = false
            radioDark.isChecked = true
        }
    }

    private fun loadKmMiSelection(settings: UserSettings) {
        if (settings.isKmUnit) {
            radioKm.isChecked = true
            radioMi.isChecked = false
        } else {
            radioKm.isChecked = false
            radioMi.isChecked = true
        }
    }

    private fun switchTheme() {
        when {
            radioDark.isChecked -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            radioLight.isChecked -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun getSelectedTheme(): String {
        if (radioDark.isChecked) {
            return "dark"
        }
        if (radioLight.isChecked) {
            return "light"
        }
        return ""
    }

    private fun getKmMi(): Boolean {
        return radioKm.isChecked
    }

    private fun saveSettings() {
        val settingsData = UserSettings(
            isConfigured = true,
            language = injectLangSpinner(),
            theme = getSelectedTheme(),
            isKmUnit = getKmMi(),
            consumption = textConsumption.text.toString(),
            rented = switchRent.isChecked,
            rentCost = textRentCost.text.toString(),
            service = switchService.isChecked,
            serviceCost = textServiceCost.text.toString(),
            goalPerMonth = textGoalPerMonth.text.toString(),
            schedule = getSchedule(),
            taxes = switchTaxes.isChecked,
            taxRate = textTaxRate.text.toString(),
            fuelPrice = textFuelCost.text.toString()
        )
        viewModel.saveSettings(settingsData)
    }

    private fun switchVisualize(switch: MaterialSwitch) {
        val table: TableRow
        when (switch) {
            binding.switchRent -> {
                table = binding.TableRent
            }

            binding.switchService -> {
                table = binding.TableService
            }

            binding.switchTaxes -> {
                table = binding.TableTaxes
            }

            else -> return
        }
        animateHeightChange(table)
    }

    private fun animateHeightChange(view: View) {
        val parentLayout = view.parent as ViewGroup
        val visibility = if (view.isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(parentLayout)
        view.visibility = visibility
    }

    private fun injectLangSpinner(): String {
        when (spinnerLang.selectedItemPosition) {
            0 -> return "en"
            1 -> return "ru"
        }
        return ""
    }

    private fun getSchedule(): String {
        return when (radioSchedule.checkedRadioButtonId) {
            R.id.radio70 -> "7/0"
            R.id.radio61 -> "6/1"
            R.id.radio52 -> "5/2"
            else -> "0"
        }
    }

    private fun getScheduleId(schedule: String?): Int {
        return when (schedule) {
            "7/0" -> R.id.radio70
            "6/1" -> R.id.radio61
            "5/2" -> R.id.radio52
            else -> -1
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_VISIBLE_RENT", binding.TableRent.isVisible)
        outState.putBoolean("IS_VISIBLE_SERVICE", binding.TableService.isVisible)
        outState.putBoolean("IS_VISIBLE_TAXES", binding.TableTaxes.isVisible)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}