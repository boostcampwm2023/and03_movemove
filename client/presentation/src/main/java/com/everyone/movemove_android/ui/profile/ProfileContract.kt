package com.everyone.movemove_android.ui.profile

import com.everyone.domain.model.Profile
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.domain.model.VideosUploaded
import com.everyone.movemove_android.base.BaseContract
import com.everyone.movemove_android.ui.rating_video.RatingVideoContract

interface ProfileContract :
    BaseContract<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val uuid: String? = null,
        val profile: Profile? = null,
        val videosUploaded: VideosList = VideosList(null)
    )

    sealed interface Event {
        data object OnClickedMenu : Event
        data class OnClickedVideo(
            val videosList: VideosList,
            val page: Int
        ) : Event

    }

    sealed interface Effect {
        data object NavigateToMy : Effect
        data class NavigateToWatchingVideo(
            val videosList: VideosList,
            val page: Int
        ) : Effect
    }
}
