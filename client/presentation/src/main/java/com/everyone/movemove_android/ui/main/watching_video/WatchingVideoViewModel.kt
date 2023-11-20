package com.everyone.movemove_android.ui.main.watching_video

import androidx.lifecycle.ViewModel
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Category
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Event.OnClickedCategory
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Event.OnSelectedCategory
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Effect
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.Event
import com.everyone.movemove_android.ui.main.watching_video.WatchingVideoContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WatchingVideoViewModel @Inject constructor() : ViewModel(), WatchingVideoContract {
    private val _state = MutableStateFlow(State())
    override val state: StateFlow<State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<Effect>()
    override val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    override fun event(event: Event) {
        when (event) {
            is OnClickedCategory -> onClickedCategory()
            is OnSelectedCategory -> onClickedCategory(selectedCategory = event.selectedCategory)
        }
    }

    private fun onClickedCategory() {
        _state.update {
            it.copy(isClickedCategory = !it.isClickedCategory)
        }
    }

    private fun onClickedCategory(selectedCategory: Category) {
        _state.update {
            it.copy(
                isClickedCategory = !it.isClickedCategory,
                selectedCategory = selectedCategory
            )
        }
    }
}