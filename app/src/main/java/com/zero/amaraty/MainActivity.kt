package com.zero.amaraty

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zero.amaraty.data.settings.AppSettings
import com.zero.amaraty.data.settings.SettingsRepository
import com.zero.amaraty.ui.AmaratyApp
import com.zero.amaraty.ui.LockScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    private var currentSettings = AppSettings()
    private var unlocked = false
    private var promptActive = false

    private val settingsState = androidx.compose.runtime.mutableStateOf(AppSettings())
    private val loadedState = androidx.compose.runtime.mutableStateOf(false)
    private val unlockedState = androidx.compose.runtime.mutableStateOf(false)
    private val lockMessageState = androidx.compose.runtime.mutableStateOf<String?>(null)
    private val settingsFailureState = androidx.compose.runtime.mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.settings
                    .catch {
                        settingsFailureState.value = true
                        lockMessageState.value = getString(R.string.settings_load_failed)
                        loadedState.value = true
                    }
                    .collect { settings ->
                        val wasEnabled = currentSettings.appLockEnabled
                        currentSettings = settings
                        settingsState.value = settings
                        settingsFailureState.value = false
                        loadedState.value = true
                        if (!settings.appLockEnabled) {
                            unlocked = true
                            unlockedState.value = true
                            lockMessageState.value = null
                        } else if (!wasEnabled) {
                            unlocked = false
                            unlockedState.value = false
                        }
                    }
            }
        }

        setContent {
            val settings = settingsState.value
            val isLoaded = loadedState.value
            val isUnlocked = unlockedState.value
            val message = lockMessageState.value
            val settingsFailed = settingsFailureState.value

            androidx.compose.runtime.LaunchedEffect(settings.appLockEnabled, isUnlocked, isLoaded, settingsFailed) {
                if (isLoaded && !settingsFailed && settings.appLockEnabled && !isUnlocked) {
                    authenticateUser()
                }
            }

            if (!isLoaded) {
                LockScreen(
                    message = getString(R.string.loading_settings),
                    actionLabel = null,
                    onUnlock = {},
                )
            } else if (settingsFailed) {
                LockScreen(
                    message = message ?: getString(R.string.settings_load_failed),
                    actionLabel = getString(R.string.retry),
                    onUnlock = { recreate() },
                )
            } else if (settings.appLockEnabled && !isUnlocked) {
                LockScreen(
                    message = message ?: getString(R.string.lock_prompt),
                    actionLabel = getString(R.string.unlock_button),
                    onUnlock = ::authenticateUser,
                )
            } else {
                AmaratyApp()
            }
        }
    }

    override fun onStop() {
        if (currentSettings.appLockEnabled && !isChangingConfigurations) {
            unlocked = false
            unlockedState.value = false
        }
        super.onStop()
    }

    private fun allowedAuthenticators(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    } else {
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    }

    private fun authenticateUser() {
        if (promptActive || isFinishing || !currentSettings.appLockEnabled) return

        val authenticators = allowedAuthenticators()
        val availability = BiometricManager.from(this).canAuthenticate(authenticators)
        if (availability != BiometricManager.BIOMETRIC_SUCCESS) {
            lockMessageState.value = getString(R.string.lock_unavailable)
            return
        }

        promptActive = true
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    promptActive = false
                    unlocked = true
                    unlockedState.value = true
                    lockMessageState.value = null
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    promptActive = false
                    lockMessageState.value = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    lockMessageState.value = getString(R.string.lock_failed)
                }
            },
        )
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.lock_title))
            .setSubtitle(
                getString(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        R.string.lock_subtitle
                    } else {
                        R.string.lock_subtitle_biometric
                    },
                ),
            )
            .setAllowedAuthenticators(authenticators)
            .build()
        prompt.authenticate(promptInfo)
    }
}
