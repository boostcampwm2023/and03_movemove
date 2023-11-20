package com.everyone.movemove_android.ui.main.watching_video

import com.everyone.movemove_android.base.BaseContract

interface WatchingVideoContract :
    BaseContract<WatchingVideoContract.State, WatchingVideoContract.Event, WatchingVideoContract.Effect> {
    data class State(
        val isClickedCategory: Boolean = false,
        val selectedCategory: Category = Category.TOTAL,
        val categoryList: List<Category> = listOf(
            Category.TOTAL,
            Category.CHALLENGE,
            Category.OLD_SCHOOL,
            Category.NEW_SCHOOL,
            Category.K_POP
        )
    )

    sealed interface Event {
        data object OnClickedCategory : Event
        data class OnCategorySelected(val selectedCategory: Category) : Event
    }

    sealed interface Effect {}

    enum class Category(val categoryName: String) {
        TOTAL("전체"),
        CHALLENGE("챌린지"),
        OLD_SCHOOL("올드스쿨"),
        NEW_SCHOOL("뉴스쿨"),
        K_POP("K-POP")
    }
}
