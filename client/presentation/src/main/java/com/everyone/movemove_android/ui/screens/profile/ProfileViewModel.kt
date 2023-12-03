package com.everyone.movemove_android.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.*
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Effect.NavigateToMy
import com.everyone.movemove_android.ui.screens.profile.ProfileContract.Event.OnClickedMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel(), ProfileContract {
    private val _state = MutableStateFlow(State())
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()
    override fun event(event: Event) = when (event) {
        is OnClickedMenu -> onClickedMenu()

    }

    private fun onClickedMenu() {
        viewModelScope.launch {
            _effect.emit(NavigateToMy)
        }
    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }
}