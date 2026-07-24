package com.hsact.taxilog.ui.activities.settings

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.model.settings.indexToCurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.auth.GoogleAuthClient
import com.hsact.taxilog.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.user.collect { user ->
                binding.textUserEmail.text = user?.email ?: getString(R.string.not_signed_in)
                if (user == null) {
                    binding.buttonSignOut.text = getString(R.string.sign_in)
                    binding.buttonSignOut.setOnClickListener {
                        login()
                    }
                } else {
                    binding.buttonSignOut.text = getString(R.string.sign_out)
                    binding.buttonSignOut.setOnClickListener {
                        logout()
                    }
                }
            }
        }

        displayCurrencySymbol()
        updateUiWithSettings()
        updateTableVisibility(binding.switchRent)
        updateTableVisibility(binding.switchService)
        updateTableVisibility(binding.switchTaxes)

        savedInstanceState?.let {
            binding.TableRent.isVisible = it.getBoolean("IS_VISIBLE_RENT", false)
            binding.TableService.isVisible = it.getBoolean("IS_VISIBLE_SERVICE", false)
            binding.TableTaxes.isVisible = it.getBoolean("IS_VISIBLE_TAXES", false)
        }

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                displayCurrencySymbol(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.switchRent.setOnCheckedChangeListener { _, _ -> toggleTableVisibility(binding.switchRent) }
        binding.switchService.setOnCheckedChangeListener { _, _ -> toggleTableVisibility(binding.switchService) }
        binding.switchTaxes.setOnCheckedChangeListener { _, _ -> toggleTableVisibility(binding.switchTaxes) }

        binding.buttonApply.setOnClickListener {
            val isChanged = applySettings()
            if (findNavController().navigateUp()) {
                if (isChanged) {
                    // Slight delay to let the slide animation finish before recreation
                    binding.root.postDelayed({
                        activity?.recreate()
                    }, 400)
                }
            } else {
                if (isChanged) activity?.recreate()
            }
        }
    }

    private fun displayCurrencySymbol(currencySymbol: String = getCurrencySymbol()) {
        binding.editTextFuelPriceL.hint = getString(R.string.settings_fuel_l) + "/" + currencySymbol
        binding.editTextRentCostL.hint = currencySymbol + "/" + getString(R.string.hint_money_per_shift)
        binding.editTextServiceCostL.hint = currencySymbol + "/" + getString(R.string.hint_money_per_km_mi)
        binding.editTextGoalPerMonthL.hint = currencySymbol
    }

    private fun getCurrencySymbol(): String = (viewModel.settings.value?.currency?.toSymbol()
        ?: CurrencySymbolMode.fromLocale(Locale.getDefault()).toSymbol())

    private fun login() {
        viewModel.setAuthSkipped(false)
        lifecycleScope.launch {
            val result = googleAuthClient.signInAndAuthenticate(requireActivity())
            result.onSuccess {
                // Success is handled by the user state flow observer
            }.onFailure {
                showRetryDialog()
            }
        }
    }

    private fun logout() {
        viewModel.signOut()
    }

    private fun showRetryDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.authentication_failed))
            .setMessage(getString(R.string.retry_login_question))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                login()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .show()
    }

    private fun applySettings(): Boolean {
        val selectedLang = getSelectedLanguage()
        val selectedTheme = getSelectedTheme()
        val currentSettings = viewModel.settings.value

        val langChanged = selectedLang != currentSettings?.language
        val themeChanged = selectedTheme != currentSettings?.theme

        if (langChanged) {
            viewModel.localeHelper.setLocale(requireContext(), selectedLang)
        }

        saveSettings()

        if (themeChanged) {
            switchTheme(selectedTheme)
        }

        return langChanged || themeChanged
    }

    private fun updateUiWithSettings() {
        val settings = viewModel.settings.value ?: return
        setupLanguageSpinner(settings.language)
        
        val firstDayValue = if (settings.firstDayOfWeek > 0) {
            settings.firstDayOfWeek
        } else {
            WeekFields.of(Locale.getDefault()).firstDayOfWeek.value
        }
        binding.spinnerFirstDayOfWeek.setSelection(firstDayValue - 1)

        setupCurrencySpinner(settings.currency)
        updateThemeRadioButtons(settings.theme)
        if (!settings.isConfigured) return

        updateDistanceUnitRadioButtons(settings)
        binding.editTextConsumption.setText(settings.consumption)
        binding.switchRent.isChecked = settings.rented
        binding.editTextRentCost.setText(settings.rentCost)
        binding.editTextFuelPrice.setText(settings.fuelPrice)
        binding.switchService.isChecked = settings.service
        binding.editTextServiceCost.setText(settings.serviceCost)
        binding.editTextGoalPerMonth.setText(settings.goalPerMonth)
        binding.radioSchedule.check(getScheduleRadioButtonId(settings.schedule))
        binding.switchTaxes.isChecked = settings.taxes
        binding.editTextTaxRate.setText(settings.taxRate)
    }

    private fun setupLanguageSpinner(language: String?) {
        val currentLang = language ?: Locale.getDefault().language
        if (currentLang == "en") binding.spinnerLang.setSelection(0)
        if (currentLang == "ru") binding.spinnerLang.setSelection(1)
    }

    private fun setupCurrencySpinner(currency: CurrencySymbolMode?) {
        binding.spinnerCurrency.setSelection(
            currency?.toIndex() ?: CurrencySymbolMode.fromLocale(Locale.getDefault()).toIndex()
        )
    }

    private fun updateThemeRadioButtons(theme: String?) {
        when (theme) {
            "light" -> {
                binding.radioLight.isChecked = true
                binding.radioDark.isChecked = false
                binding.radioDefault.isChecked = false
            }
            "dark" -> {
                binding.radioLight.isChecked = false
                binding.radioDark.isChecked = true
                binding.radioDefault.isChecked = false
            }
            else -> {
                binding.radioLight.isChecked = false
                binding.radioDark.isChecked = false
                binding.radioDefault.isChecked = true
            }
        }
    }

    private fun updateDistanceUnitRadioButtons(settings: UserSettings) {
        binding.radioKm.isChecked = settings.isKmUnit
        binding.radioMi.isChecked = !settings.isKmUnit
    }

    private fun switchTheme(selectedTheme: String) {
        val mode = when (selectedTheme) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun getSelectedTheme(): String {
        return if (binding.radioDark.isChecked) "dark" else if (binding.radioLight.isChecked) "light" else ""
    }

    private val isKmUnitSelected get() = binding.radioKm.isChecked

    private fun saveSettings() {
        val settingsData = UserSettings(
            isConfigured = true,
            language = getSelectedLanguage(),
            theme = getSelectedTheme(),
            currency = getSelectedCurrency(),
            isKmUnit = isKmUnitSelected,
            consumption = binding.editTextConsumption.text.toString(),
            rented = binding.switchRent.isChecked,
            rentCost = binding.editTextRentCost.text.toString(),
            service = binding.switchService.isChecked,
            serviceCost = binding.editTextServiceCost.text.toString(),
            goalPerMonth = binding.editTextGoalPerMonth.text.toString(),
            schedule = getSelectedSchedule(),
            taxes = binding.switchTaxes.isChecked,
            taxRate = binding.editTextTaxRate.text.toString(),
            fuelPrice = binding.editTextFuelPrice.text.toString(),
            firstDayOfWeek = binding.spinnerFirstDayOfWeek.selectedItemPosition + 1
        )
        viewModel.saveSettings(settingsData)
    }

    private fun updateTableVisibility(switch: MaterialSwitch) {
        val table = when (switch) {
            binding.switchRent -> binding.TableRent
            binding.switchService -> binding.TableService
            binding.switchTaxes -> binding.TableTaxes
            else -> return
        }
        table.isVisible = switch.isChecked
    }

    private fun toggleTableVisibility(switch: MaterialSwitch) {
        val table = when (switch) {
            binding.switchRent -> binding.TableRent
            binding.switchService -> binding.TableService
            binding.switchTaxes -> binding.TableTaxes
            else -> return
        }
        TransitionManager.beginDelayedTransition(binding.layoutSettings)
        table.isVisible = switch.isChecked
    }

    private fun getSelectedCurrency(): CurrencySymbolMode {
        return binding.spinnerCurrency.selectedItemPosition.indexToCurrencySymbolMode()
    }

    private fun getSelectedLanguage(): String {
        return when (binding.spinnerLang.selectedItemPosition) {
            0 -> "en"
            1 -> "ru"
            else -> ""
        }
    }

    private fun getSelectedSchedule(): String {
        return when (binding.radioSchedule.checkedRadioButtonId) {
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
        _binding?.let {
            outState.putBoolean("IS_VISIBLE_RENT", it.TableRent.isVisible)
            outState.putBoolean("IS_VISIBLE_SERVICE", it.TableService.isVisible)
            outState.putBoolean("IS_VISIBLE_TAXES", it.TableTaxes.isVisible)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}