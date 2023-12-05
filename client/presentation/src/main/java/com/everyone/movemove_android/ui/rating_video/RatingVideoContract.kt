package com.everyone.movemove_android.ui.rating_video

import com.everyone.domain.model.Profile
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.VideosUploaded
import com.everyone.movemove_android.base.BaseContract

interface RatingVideoContract :
    BaseContract<RatingVideoContract.State, RatingVideoContract.Event, RatingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val profile: Profile = Profile(),
        val videosUploaded: VideosUploaded = VideosUploaded(null, emptyList())
    )

    sealed interface Event {
        data object OnClickedBack : Event
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
