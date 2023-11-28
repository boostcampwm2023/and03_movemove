package com.everyone.movemove_android.ui.screens.watching_video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everyone.domain.usecase.GetVideoRandomUseCase
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Category
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnClickedCategory
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event.OnCategorySelected
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Effect
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.Event
import com.everyone.movemove_android.ui.screens.watching_video.WatchingVideoContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchingVideoViewModel @Inject constructor() : ViewModel(), WatchingVideoContract {
class WatchingVideoViewModel @Inject constructor(
    private val getVideoRandomUseCase: GetVideoRandomUseCase,
) : ViewModel(), WatchingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) {
        when (event) {
            is OnClickedCategory -> onClickedCategory()
            is OnCategorySelected -> onCategorySelected(selectedCategory = event.selectedCategory)
        }
    }

    private fun onClickedCategory() {
        _state.update {
            it.copy(isClickedCategory = !it.isClickedCategory)
        }
    }

    private fun onCategorySelected(selectedCategory: Category) {
        _state.update {
            it.copy(
                isClickedCategory = !it.isClickedCategory,
                selectedCategory = selectedCategory
            )
        }
    }
}