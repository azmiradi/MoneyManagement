package com.demo.moneymanagement.presentation.screens.auth.splash

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.preferences.general.GeneralGeneralPrefsStoreImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val generalGeneralPrefsStoreImpl: GeneralGeneralPrefsStoreImpl
) : ViewModel() {


    private var job: Job? = null
    private val _state = mutableStateOf(SplashChecker<String>())
    val state: State<SplashChecker<String>> = _state

    init {
        viewModelScope.launch {
            delay(3000)
            checkLogin()
        }

    }

    private fun checkLogin() {
        _state.value = SplashChecker()
        job?.cancel()
        job = generalGeneralPrefsStoreImpl.getID()
            .onEach {
                _state.value = SplashChecker(login = it)
            }.catch {
                _state.value = SplashChecker(notLogin = true)
            }.launchIn(viewModelScope)
    }


    fun resetState() {
        _state.value = SplashChecker()
        job?.cancel()
    }


}

data class SplashChecker<T>(
    val login: T? = null,
    val notLogin: Boolean = false,
)