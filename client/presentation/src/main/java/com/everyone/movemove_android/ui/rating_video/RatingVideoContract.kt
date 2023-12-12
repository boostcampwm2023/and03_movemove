package com.everyone.movemove_android.ui.rating_video

import com.everyone.domain.model.VideosList
import com.everyone.domain.model.VideosRated
import com.everyone.movemove_android.base.BaseContract

interface RatingVideoContract :
    BaseContract<RatingVideoContract.State, RatingVideoContract.Event, RatingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val videosRated: VideosRated? = null
    )

    sealed interface Event {
        data object OnClickedBack : Event
        data object Refresh : Event
        data class OnClickedVideo(
            val videosLit: VideosList,
            val page: Int
        ) : Event
    }

    sealed interface Effect {
        data object OnClickedBack : Effect
        data class OnClickedVideo(
            val videosList: VideosList,
            val page: Int
        ) : Effect
    }
}
