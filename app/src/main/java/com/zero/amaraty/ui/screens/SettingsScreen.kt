package com.zero.amaraty.ui.screens

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zero.amaraty.R
import com.zero.amaraty.SettingsFailure
import com.zero.amaraty.data.settings.AppSettings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settings: AppSettings,
    failure: SettingsFailure?,
    onClearFailure: () -> Unit,
    onArabicDigitsChanged: (Boolean) -> Unit,
    onDarkThemeChanged: (Boolean) -> Unit,
    onAppLockChanged: (Boolean) -> Unit,
    onSecureScreensChanged: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authMethods = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    } else {
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    }
    val biometricAvailable = remember {
        BiometricManager.from(context).canAuthenticate(authMethods) == BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(failure) {
        when (failure) {
            SettingsFailure.LOAD_FAILED -> snackbarHostState.showSnackbar(context.getString(R.string.settings_load_failed))
            SettingsFailure.SAVE_FAILED -> snackbarHostState.showSnackbar(context.getString(R.string.settings_save_failed))
            null -> Unit
        }
        if (failure != null) onClearFailure()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
        Text(text = stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineSmall)
        SnackbarHost(hostState = snackbarHostState)
        SettingRow(
            title = stringResource(R.string.arabic_digits_title),
            description = stringResource(R.string.arabic_digits_description),
            checked = settings.useArabicDigits,
            onCheckedChange = onArabicDigitsChanged,
        )
        SettingRow(
            title = stringResource(R.string.dark_theme_title),
            description = stringResource(R.string.dark_theme_description),
            checked = settings.darkTheme,
            onCheckedChange = onDarkThemeChanged,
        )
        SettingRow(
            title = stringResource(R.string.secure_screens_title),
            description = stringResource(R.string.secure_screens_description),
            checked = settings.secureScreens,
            onCheckedChange = onSecureScreensChanged,
        )
        SettingRow(
            title = stringResource(R.string.app_lock_title),
            description = stringResource(R.string.app_lock_description),
            checked = settings.appLockEnabled,
            onCheckedChange = { enabled ->
                if (!enabled || biometricAvailable) {
                    onAppLockChanged(enabled)
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.lock_setup_required))
                    }
                }
            },
        )
        Text(
            text = stringResource(R.string.settings_local_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
