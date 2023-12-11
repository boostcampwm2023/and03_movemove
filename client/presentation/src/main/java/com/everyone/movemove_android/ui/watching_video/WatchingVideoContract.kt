package com.everyone.movemove_android.ui.watching_video

import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosList
import com.everyone.movemove_android.base.BaseContract

interface WatchingVideoContract :
    BaseContract<WatchingVideoContract.State, WatchingVideoContract.Event, WatchingVideoContract.Effect> {
    data class State(
        val isLoading: Boolean = false,
        val isClickedCategory: Boolean = false,
        val selectedCategory: Category = Category.TOTAL,
        val categoryList: List<Category> = listOf(
            Category.TOTAL,
            Category.CHALLENGE,
            Category.OLD_SCHOOL,
            Category.NEW_SCHOOL,
            Category.K_POP
        ),
        val videos: List<Videos>? = null,
        val page: Int = 0,
        val seed: String = "",
        val videoTab: VideoTab = VideoTab.CATEGORY_TAB
    )

    sealed interface Event {
        data object OnClickedCategory : Event
        data class OnCategorySelected(val selectedCategory: Category) : Event
        data object GetRandomVideos : Event
        data class OnClickedVideoRating(
            val id: String,
            val rating: String,
            val reason: String
        ) : Event

        data class PutVideosViews(val videoId: String) : Event
        data class OnClickedProfile(val uuid: String) : Event

    }

    sealed interface Effect {
        data class NavigateToProfile(val uuid: String) : Effect
    }

    enum class Category(val displayName: String) {
        TOTAL("전체"),
        CHALLENGE("챌린지"),
        OLD_SCHOOL("올드스쿨"),
        NEW_SCHOOL("뉴스쿨"),
        K_POP("Kpop")
    }

    enum class VideoTab {
        CATEGORY_TAB,
        BOTTOM_TAB
    }
}
