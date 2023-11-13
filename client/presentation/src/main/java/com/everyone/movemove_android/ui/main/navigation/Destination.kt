package com.everyone.movemove_android.ui.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.everyone.movemove_android.R


enum class Destination(
    val route: String,
    @DrawableRes val iconRes: Int,
    @StringRes val labelResId: Int,
) {
    HOME(
        route = ROUTE_HOME,
        iconRes = R.drawable.ic_home,
        labelResId = R.string.navigation_home
    ),
    VIDEO(
        route = ROUTE_VIDEO,
        iconRes = R.drawable.ic_video,
        labelResId = R.string.navigation_video
    ),
    VIDEO_ADD(
        route = ROUTE_VIDEO_ADD,
        iconRes = R.drawable.ic_video_add,
        labelResId = R.string.navigation_video_add
    ),
    MY(
        route = ROUTE_MY,
        iconRes = R.drawable.ic_my,
        labelResId = R.string.navigation_my
    )
}

private const val ROUTE_HOME = "home"
private const val ROUTE_VIDEO = "video"
private const val ROUTE_VIDEO_ADD = "videoAdd"
private const val ROUTE_MY = "my"