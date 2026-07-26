package com.hsact.taxilog.ui.fragments.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.hsact.domain.model.User
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.model.settings.indexToCurrencySymbolMode
import com.hsact.taxilog.R
import java.time.temporal.WeekFields

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    user: User?,
    onSignOutClick: () -> Unit,
    onSignInClick: () -> Unit,
    onUpdateSettings: (UserSettings) -> Unit,
    onApplyClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            is SettingsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SettingsUiState.Success -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    val scrollState = rememberScrollState()
                    var isFabVisible by remember { mutableStateOf(true) }
                    var lastScrollValue by remember { mutableIntStateOf(0) }
                    
                    val density = LocalDensity.current
                    var fabHeightDp by remember { mutableStateOf(0.dp) }

                    LaunchedEffect(scrollState.value) {
                        val current = scrollState.value
                        val isAtBottom = scrollState.maxValue in 1..current
                        val isAtTop = current <= 0
                        val isScrollingUp = current < lastScrollValue
                        
                        isFabVisible = isAtTop || isAtBottom || isScrollingUp
                        lastScrollValue = current
                    }
                    
                    SettingsContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        settings = uiState.settings,
                        user = user,
                        onSignOutClick = onSignOutClick,
                        onSignInClick = onSignInClick,
                        onUpdateSettings = onUpdateSettings,
                        bottomPadding = fabHeightDp
                    )

                    // Floating Apply Button
                    AnimatedVisibility(
                        visible = isFabVisible,
                        enter = slideInVertically(initialOffsetY = { it * 2 }),
                        exit = slideOutVertically(targetOffsetY = { it * 2 }),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .onGloballyPositioned { coordinates ->
                                fabHeightDp = with(density) { coordinates.size.height.toDp() }
                            }
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = onApplyClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            expanded = true, // We want it wide
                            icon = {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.apply),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    settings: UserSettings,
    user: User?,
    onSignOutClick: () -> Unit,
    onSignInClick: () -> Unit,
    onUpdateSettings: (UserSettings) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val configuration = LocalConfiguration.current
    val currentLocale = configuration.locales[0]

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val currencyMode = settings.currency ?: CurrencySymbolMode.fromLocale(currentLocale)
        val currencySymbol = currencyMode.toSymbol()
        val isPrefix = currencyMode.isPrefix
        val distanceUnit = if (settings.isKmUnit) "km" else "mi"

        // Helper function for money field prefix
        val moneyPrefix: @Composable (() -> Unit)? = if (isPrefix) {
            { Text(currencySymbol) }
        } else null

        // Helper function for money field suffix with optional unit
        fun moneySuffix(unit: String? = null): @Composable (() -> Unit) = {
            val text = when {
                !isPrefix && unit != null -> "$currencySymbol $unit"
                !isPrefix -> currencySymbol
                unit != null -> unit
                else -> ""
            }
            if (text.isNotEmpty()) Text(text)
        }

        // User Section
        UserSection(user, onSignOutClick, onSignInClick)

        // Interface Section
        SettingsSection(title = stringResource(R.string.settings_interface)) {
            LanguageSelector(
                selectedLanguage = settings.language,
                onLanguageSelected = { onUpdateSettings(settings.copy(language = it)) }
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            FirstDayOfWeekSelector(
                selectedDay = settings.firstDayOfWeek,
                onDaySelected = { onUpdateSettings(settings.copy(firstDayOfWeek = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            CurrencySelector(
                selectedCurrency = settings.currency,
                onCurrencySelected = { onUpdateSettings(settings.copy(currency = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            ThemeSelector(
                selectedTheme = settings.theme,
                onThemeSelected = { onUpdateSettings(settings.copy(theme = it)) }
            )
        }

        // Car Section
        SettingsSection(title = stringResource(R.string.car)) {
            OutlinedTextField(
                value = settings.consumption ?: "",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                    onUpdateSettings(settings.copy(consumption = filtered))
                },
                label = { Text(stringResource(R.string.settings_consumption)) },
                suffix = { Text(stringResource(R.string.settings_consumption_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = settings.fuelPrice ?: "",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                    onUpdateSettings(settings.copy(fuelPrice = filtered))
                },
                label = { Text(stringResource(R.string.settings_fuel_price)) },
                prefix = moneyPrefix,
                suffix = moneySuffix("/" + stringResource(R.string.liter_short)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Column {
                SwitchRow(
                    label = stringResource(R.string.settings_rented_car),
                    checked = settings.rented,
                    onCheckedChange = { onUpdateSettings(settings.copy(rented = it)) }
                )

                AnimatedVisibility(visible = settings.rented) {
                    OutlinedTextField(
                        value = settings.rentCost ?: "",
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                            onUpdateSettings(settings.copy(rentCost = filtered))
                        },
                        label = { Text(stringResource(R.string.settings_rent_cost)) },
                        prefix = moneyPrefix,
                        suffix = moneySuffix("/" + stringResource(R.string.shift).lowercase()),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Column {
                SwitchRow(
                    label = stringResource(R.string.settings_calculate_service),
                    checked = settings.service,
                    onCheckedChange = { onUpdateSettings(settings.copy(service = it)) }
                )

                AnimatedVisibility(visible = settings.service) {
                    OutlinedTextField(
                        value = settings.serviceCost ?: "",
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                            onUpdateSettings(settings.copy(serviceCost = filtered))
                        },
                        label = { Text(stringResource(R.string.settings_service_cost_per_km)) },
                        prefix = moneyPrefix,
                        suffix = moneySuffix("/$distanceUnit"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }

        // Goal Section
        SettingsSection(title = stringResource(R.string.settings_plan)) {
            OutlinedTextField(
                value = settings.goalPerMonth ?: "",
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                    onUpdateSettings(settings.copy(goalPerMonth = filtered))
                },
                label = { Text(stringResource(R.string.settings_goal_per_month)) },
                prefix = moneyPrefix,
                suffix = moneySuffix(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            ScheduleSelector(
                selectedSchedule = settings.schedule,
                onScheduleSelected = { onUpdateSettings(settings.copy(schedule = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Column {
                SwitchRow(
                    label = stringResource(R.string.settings_calculate_taxes),
                    checked = settings.taxes,
                    onCheckedChange = { onUpdateSettings(settings.copy(taxes = it)) }
                )

                AnimatedVisibility(visible = settings.taxes) {
                    OutlinedTextField(
                        value = settings.taxRate ?: "",
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '-' }
                            onUpdateSettings(settings.copy(taxRate = filtered))
                        },
                        label = { Text(stringResource(R.string.settings_tax_rate)) },
                        suffix = { Text(stringResource(R.string.percent)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(bottomPadding))
    }
}

@Composable
private fun UserSection(
    user: User?,
    onSignOutClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var isLoading by remember { mutableStateOf(true) }
            
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onState = { state ->
                    isLoading = state is AsyncImagePainter.State.Loading
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .then(if (isLoading) Modifier.shimmerLoadingAnimation() else Modifier)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user?.displayName ?: stringResource(R.string.not_signed_in),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                val email = user?.email
                if (email != null) {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Button(
                onClick = if (user != null) onSignOutClick else onSignInClick,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    if (user != null) stringResource(R.string.sign_out) else stringResource(R.string.sign_in),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    selectedLanguage: String?,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = stringArrayResource(R.array.languages)
    val langCodes = listOf("en", "ru")
    
    val currentLocale = LocalConfiguration.current.locales[0]
    val currentLangCode = selectedLanguage ?: currentLocale.language
    val selectedIndex = if (currentLangCode == "ru") 1 else 0

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = languages[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.settings_language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageSelected(langCodes[index])
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FirstDayOfWeekSelector(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val days = stringArrayResource(R.array.days_of_week)
    
    val currentLocale = LocalConfiguration.current.locales[0]
    val firstDayValue = if (selectedDay > 0) {
        selectedDay
    } else {
        WeekFields.of(currentLocale).firstDayOfWeek.value
    }
    val selectedIndex = (firstDayValue - 1).coerceIn(0, 6)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = days[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.settings_first_day_of_week)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            days.forEachIndexed { index, day ->
                DropdownMenuItem(
                    text = { Text(day) },
                    onClick = {
                        onDaySelected(index + 1)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencySelector(
    selectedCurrency: CurrencySymbolMode?,
    onCurrencySelected: (CurrencySymbolMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = stringArrayResource(R.array.currencies)
    val selectedIndex = selectedCurrency?.toIndex() ?: 0

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = currencies[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEachIndexed { index, currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(index.indexToCurrencySymbolMode())
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(
    selectedTheme: String?,
    onThemeSelected: (String) -> Unit
) {
    val options = listOf(
        stringResource(R.string.settings_auto) to "",
        stringResource(R.string.settings_light) to "light",
        stringResource(R.string.settings_dark) to "dark"
    )
    val selectedIndex = when (selectedTheme) {
        "light" -> 1
        "dark" -> 2
        else -> 0
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.bodyMedium
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (label, value) ->
                SegmentedButton(
                    selected = selectedIndex == index,
                    onClick = { onThemeSelected(value) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                        inactiveBorderColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleSelector(
    selectedSchedule: String?,
    onScheduleSelected: (String) -> Unit
) {
    val schedules = listOf("7/0", "6/1", "5/2")
    val selectedIndex = schedules.indexOf(selectedSchedule).coerceAtLeast(0)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.settings_schedule),
            style = MaterialTheme.typography.bodyMedium
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            schedules.forEachIndexed { index, schedule ->
                SegmentedButton(
                    selected = selectedIndex == index,
                    onClick = { onScheduleSelected(schedule) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = schedules.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                        inactiveBorderColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(schedule, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun Modifier.shimmerLoadingAnimation(): Modifier = composed {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant,
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    this.background(brush)
}