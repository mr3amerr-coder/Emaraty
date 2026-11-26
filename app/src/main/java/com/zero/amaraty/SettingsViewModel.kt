package com.zero.amaraty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zero.amaraty.data.settings.AppSettings
import com.zero.amaraty.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


enum class SettingsFailure {
    LOAD_FAILED,
    SAVE_FAILED,
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
) : ViewModel() {
    private val mutableSettings = MutableStateFlow(AppSettings())
    private val mutableFailure = MutableStateFlow<SettingsFailure?>(null)

    val settings: StateFlow<AppSettings> = mutableSettings.asStateFlow()
    val failure: StateFlow<SettingsFailure?> = mutableFailure.asStateFlow()

    init {
        viewModelScope.launch {
            repository.settings
                .catch { exception ->
                    if (exception is CancellationException) throw exception
                    mutableFailure.value = SettingsFailure.LOAD_FAILED
                }
                .collect { current ->
                    mutableSettings.value = current
                    if (mutableFailure.value == SettingsFailure.LOAD_FAILED) {
                        mutableFailure.value = null
                    }
                }
        }
    }

    fun setArabicDigits(enabled: Boolean) = update { repository.setArabicDigits(enabled) }

    fun setDarkTheme(enabled: Boolean) = update { repository.setDarkTheme(enabled) }

    fun setAppLock(enabled: Boolean) = update { repository.setAppLock(enabled) }

    fun setSecureScreens(enabled: Boolean) = update { repository.setSecureScreens(enabled) }

    fun clearFailure() {
        mutableFailure.value = null
    }

    private fun update(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                mutableFailure.value = null
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                mutableFailure.value = SettingsFailure.SAVE_FAILED
            }
        }
    }
}
