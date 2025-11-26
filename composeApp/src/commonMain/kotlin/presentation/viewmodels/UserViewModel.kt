package presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.appconfig.AppPreferences
import kotlinx.coroutines.launch

class UserViewModel(
    private val appPreferences: AppPreferences
): ViewModel() {
    private val _state = mutableStateOf(UserState())
    val state: State<UserState> = _state

    init {
        viewModelScope.launch {
            appPreferences.observeIsSyncEnabled().collect {
                _state.value = state.value.copy(isSyncEnabled = it)
            }
        }
        viewModelScope.launch {
            appPreferences.observeSyncEmailAddress().collect {
                _state.value = state.value.copy(syncEmailAddress = it)
            }
        }
        viewModelScope.launch {
            appPreferences.observeIsSyncEmailConfirmed().collect {
                _state.value = state.value.copy(isSyncConfirmed = it)
            }
        }
    }

    fun setIsSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setIsSyncEnabled(enabled)
        }
    }

    fun setSyncEmailAddress(emailAddress: String) {
        viewModelScope.launch {
            appPreferences.setSyncEmailAddress(emailAddress)
        }
    }
}

data class UserState(
    val syncEmailAddress: String? = null,
    val isSyncConfirmed: Boolean = false,
    val isSyncEnabled: Boolean = false,
)