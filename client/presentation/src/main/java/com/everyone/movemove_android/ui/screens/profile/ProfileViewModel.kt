package com.everyone.movemove_android.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel(), ProfileContract {
    private val _state = MutableStateFlow(State())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()
    override fun event(event: Event) = when (event) {
        is Event.LoadingStart -> loadingStart()
        is Event.LoadingEnd -> loadingEnd()

    }

    private fun loadingStart() {
        _state.update {
            it.copy(isLoading = true)
        }
    }

    private fun loadingEnd() {
        _state.update {
            it.copy(isLoading = false)
        }
    }

}