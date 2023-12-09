package com.everyone.movemove_android.ui.screens.home

import com.everyone.domain.model.Advertisements
import com.everyone.domain.model.VideosList
import com.everyone.movemove_android.base.BaseContract

interface HomeContract :
    BaseContract<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val advertisements: Advertisements = Advertisements(null),
        val videosTrend: VideosList = VideosList(null),
        val videosTopRatedOldSchool: VideosList = VideosList(null),
        val videosTopRatedChallenge: VideosList = VideosList(null),
        val isErrorDialogShowing: Boolean = false,
        val errorDialogTextResourceId: Int = 0
    )

    sealed interface Event {
        data class OnClickedVideo(
            val videosList: VideosList,
            val page: Int
        ) : Event

        data object OnErrorDialogDismissed : Event
    }

    sealed interface Effect {
        data class NavigateToWatchingVideo(
            val videosList: VideosList,
            val page: Int
        ) : Effect
    }
}
