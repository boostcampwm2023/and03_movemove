package com.everyone.movemove_android.ui.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.model.base.DataState
import com.everyone.domain.usecase.GetProfileUseCase
import com.everyone.movemove_android.di.IoDispatcher
import com.everyone.movemove_android.ui.my.MyContract.Effect
import com.everyone.movemove_android.ui.my.MyContract.Event
import com.everyone.movemove_android.ui.my.MyContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel(), MyContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) {}

    init {
        getProfile()
    }

    private fun getProfile() {
        loading(isLoading = true)
        getProfileUseCase("550e8400-e29b-41d4-a716-446655447000").onEach { result ->
            when (result) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            profile = result.data
                        )
                    }
                }

                is DataState.Failure -> {
                    loading(isLoading = false)
                }
            }
        }.launchIn(viewModelScope + ioDispatcher)
    }

    private fun loading(isLoading: Boolean) {
        _state.update {
            it.copy(isLoading = isLoading)
        }
    }
}