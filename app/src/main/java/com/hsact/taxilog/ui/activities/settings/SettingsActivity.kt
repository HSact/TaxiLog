package com.hsact.taxilog.ui.activities.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.model.settings.indexToCurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.auth.GoogleAuthResult
import com.hsact.taxilog.databinding.SettingsActivityBinding
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.locale.ContextWrapper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    @Inject
    lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    private val viewModel: SettingsViewModel by viewModels()

    private lateinit var binding: SettingsActivityBinding

    private lateinit var textUserEmail: TextView
    private lateinit var buttonSignOut: Button
    private lateinit var switchRent: MaterialSwitch
    private lateinit var switchService: MaterialSwitch
    private lateinit var switchTaxes: MaterialSwitch

    private lateinit var spinnerLang: Spinner
    private lateinit var spinnerCurrency: Spinner
    private lateinit var radioTheme: RadioGroup
    private lateinit var radioDefault: RadioButton
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var radioMiKm: RadioGroup
    private lateinit var radioKm: RadioButton
    private lateinit var radioMi: RadioButton
    private lateinit var textConsumption: EditText
    private lateinit var textConsumptionL: TextInputLayout
    private lateinit var textFuelCost: EditText
    private lateinit var textFuelCostL: TextInputLayout
    private lateinit var textRentCost: EditText
    private lateinit var textRentCostL: TextInputLayout
    private lateinit var textServiceCost: EditText
    private lateinit var textServiceCostL: TextInputLayout
    private lateinit var textGoalPerMonth: EditText
    private lateinit var textGoalPerMonthL: TextInputLayout
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

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            textUserEmail.text = user?.email ?: getString(R.string.not_signed_in)
//            textUserEmail.text = "example@example.com"
            if (user == null) {
                buttonSignOut.text = getString(R.string.sign_in)
                buttonSignOut.setOnClickListener {
                    login()
                }
            }
            else {
                buttonSignOut.setOnClickListener {
                    logout()
                }
            }
        }

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                googleAuthClient.handleResult(result.data) { authResult ->
                    when (authResult) {
                        is GoogleAuthResult.Success -> {
                            buttonSignOut.text = getString(R.string.sign_out)
                        }
                        is GoogleAuthResult.Error -> {
                            showRetryDialog()
                        }
                    }
                }
            }
        }

        displayCurrencySymbol()
        updateUiWithSettings()
        updateThemeRadioButtons()
        toggleTableVisibility(switchRent)
        toggleTableVisibility(switchService)
        toggleTableVisibility(switchTaxes)

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

        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                displayCurrencySymbol(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        switchRent.setOnClickListener { toggleTableVisibility(switchRent) }
        switchService.setOnClickListener { toggleTableVisibility(switchService) }
        switchTaxes.setOnClickListener { toggleTableVisibility(switchTaxes) }

        buttonApply.setOnClickListener {
            applySettings()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun displayCurrencySymbol(currencySymbol: String = getCurrencySymbol()) {
        textFuelCostL.hint = getString(R.string.settings_fuel_l) + "/" + currencySymbol
        textRentCostL.hint = currencySymbol + "/" + getString(R.string.hint_money_per_shift)
        textServiceCostL.hint = currencySymbol + "/" + getString(R.string.hint_money_per_km_mi)
        textGoalPerMonthL.hint = currencySymbol
    }

    private fun getCurrencySymbol(): String = (viewModel.settings.value?.currency?.toSymbol()
        ?: CurrencySymbolMode.fromLocale(Locale.getDefault()).toSymbol())

    private fun login() {
        viewModel.setAuthSkipped(false)
        signInLauncher.launch(googleAuthClient.getSignInIntent())
    }

    @Suppress("DEPRECATION")
    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)
        client.signOut().addOnCompleteListener {
        }
    }

    private fun showRetryDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.authentication_failed))
            .setMessage(getString(R.string.retry_login_question))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                retryGoogleSignIn()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    @Suppress("DEPRECATION")
    private fun retryGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        Handler(Looper.getMainLooper()).postDelayed({
            signInLauncher.launch(googleSignInClient.signInIntent)
        }, 0)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper.wrapContext(newBase))
    }

    private fun bindItems() {
        textUserEmail = binding.textUserEmail
        buttonSignOut = binding.buttonSignOut
        switchRent = binding.switchRent
        switchService = binding.switchService
        switchTaxes = binding.switchTaxes

        spinnerLang = binding.spinnerLang
        spinnerCurrency = binding.spinnerCurrency

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

        textFuelCostL = binding.editTextFuelPriceL
        textConsumptionL = binding.editTextConsumptionL
        textRentCostL = binding.editTextRentCostL
        textServiceCostL = binding.editTextServiceCostL
        textGoalPerMonthL = binding.editTextGoalPerMonthL

        radioSchedule = binding.radioSchedule
        radio70 = binding.radio70
        radio61 = binding.radio61
        radio52 = binding.radio52

        textTaxRate = binding.editTextTaxRate
        buttonApply = binding.buttonApply
    }

    private fun applySettings() {
        viewModel.localeHelper.setLocale(this, getSelectedLanguage())
        saveSettings()
        switchTheme()
    }

    private fun updateUiWithSettings() {
        val settings = viewModel.settings.value ?: return
        setupLanguageSpinner(settings.language)
        setupCurrencySpinner(settings.currency)
        if (!(settings.isConfigured)) {
            return
        }
        updateThemeRadioButtons()
        updateDistanceUnitRadioButtons(settings)
        textConsumption.setText(settings.consumption)
        switchRent.isChecked = settings.rented
        textRentCost.setText(settings.rentCost)
        textFuelCost.setText(settings.fuelPrice)
        switchService.isChecked = settings.service
        textServiceCost.setText(settings.serviceCost)
        textGoalPerMonth.setText(settings.goalPerMonth)
        radioSchedule.check(getScheduleRadioButtonId(settings.schedule))
        switchTaxes.isChecked = settings.taxes
        textTaxRate.setText(settings.taxRate)
    }

    private fun setupLanguageSpinner(language: String?) {
        var currentLang: String = language ?: Locale.getDefault().language
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

    private fun setupCurrencySpinner(currency: CurrencySymbolMode?) {
        spinnerCurrency.setSelection(
            currency?.toIndex() ?: CurrencySymbolMode.fromLocale(
                Locale.getDefault()
            ).toIndex()
        )
    }

    private fun updateThemeRadioButtons() {
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

    private fun updateDistanceUnitRadioButtons(settings: UserSettings) {
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

    private fun isKmUnitSelected(): Boolean {
        return radioKm.isChecked
    }

    private fun saveSettings() {
        val settingsData = UserSettings(
            isConfigured = true,
            language = getSelectedLanguage(),
            theme = getSelectedTheme(),
            currency = getSelectedCurrency(),
            isKmUnit = isKmUnitSelected(),
            consumption = textConsumption.text.toString(),
            rented = switchRent.isChecked,
            rentCost = textRentCost.text.toString(),
            service = switchService.isChecked,
            serviceCost = textServiceCost.text.toString(),
            goalPerMonth = textGoalPerMonth.text.toString(),
            schedule = getSelectedSchedule(),
            taxes = switchTaxes.isChecked,
            taxRate = textTaxRate.text.toString(),
            fuelPrice = textFuelCost.text.toString()
        )
        viewModel.saveSettings(settingsData)
    }

    private fun toggleTableVisibility(switch: MaterialSwitch) {
        val table: TableRow = when (switch) {
            binding.switchRent -> {
                binding.TableRent
            }

            binding.switchService -> {
                binding.TableService
            }

            binding.switchTaxes -> {
                binding.TableTaxes
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

    private fun getSelectedCurrency(): CurrencySymbolMode {
        return spinnerCurrency.selectedItemPosition.indexToCurrencySymbolMode()
    }

    private fun getSelectedLanguage(): String {
        when (spinnerLang.selectedItemPosition) {
            0 -> return "en"
            1 -> return "ru"
        }
        return ""
    }

    private fun getSelectedSchedule(): String {
        return when (radioSchedule.checkedRadioButtonId) {
            R.id.radio70 -> "7/0"
            R.id.radio61 -> "6/1"
            R.id.radio52 -> "5/2"
            else -> "0"
        }
    }

    private fun getScheduleRadioButtonId(schedule: String?): Int {
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