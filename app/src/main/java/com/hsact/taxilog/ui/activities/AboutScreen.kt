package com.hsact.taxilog.ui.activities

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.hsact.taxilog.R

@Composable
fun AboutScreenContent() {
    val context = LocalContext.current
    val privacyPolicyLink = stringResource(R.string.privacy_policy_link)
    val donateLink = stringResource(R.string.donate_link)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Основной контент (скроллируемый)
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                Text(
                    text = stringResource(R.string.about_app_description),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = stringResource(R.string.about_app_purpose),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = stringResource(R.string.about_features_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                val features =
                    listOf(
                        R.string.about_feature_1,
                        R.string.about_feature_2,
                        R.string.about_feature_3,
                        R.string.about_feature_4,
                        R.string.about_feature_5,
                    )

                features.forEach { featureRes ->
                    Text(
                        text = stringResource(featureRes),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 4.dp, start = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка доната
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, donateLink.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.support_project),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Футер со ссылкой на политику конфиденциальности
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.privacy_policy),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline,
                            color = colorResource(R.color.link_blue),
                        ),
                    modifier =
                        Modifier
                            .padding(bottom = 8.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, privacyPolicyLink.toUri())
                                context.startActivity(intent)
                            },
                )
            }
        }
    }
}
