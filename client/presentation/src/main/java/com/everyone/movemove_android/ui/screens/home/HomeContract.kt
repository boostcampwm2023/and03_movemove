package com.everyone.movemove_android.ui.screens.home

import com.everyone.domain.model.Advertisements
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosTrend
import com.everyone.movemove_android.base.BaseContract
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract

interface HomeContract :
    BaseContract<HomeContract.State, HomeContract.Event, HomeContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val advertisements: Advertisements = Advertisements(null),
        val videosTrend: VideosTrend = VideosTrend(null),
        val videosTopRatedOldSchool: VideosTrend = VideosTrend(null),
        val videosTopRatedChallenge: VideosTrend = VideosTrend(null)
    )

    sealed interface Event {
        data class OnClickedVideo(
            val videosTrend: VideosTrend,
            val page: Int
        ) : Event
    }

    sealed interface Effect {
        data class OnClickedVideo(
            val videosTrend: VideosTrend,
            val page: Int
        ) : Effect
    }
}
