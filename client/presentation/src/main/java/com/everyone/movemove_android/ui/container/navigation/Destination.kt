package com.everyone.movemove_android.ui.container.navigation

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
    WATCHING_VIDEO(
        route = ROUTE_VIDEO,
        iconRes = R.drawable.ic_watching_video,
        labelResId = R.string.navigation_watching_video
    ),
    UPLOADING_VIDEO(
        route = ROUTE_VIDEO_ADD,
        iconRes = R.drawable.ic_uploading_video,
        labelResId = R.string.navigation_uploading_video
    ),
    PROFILE(
        route = ROUTE_PROFILE,
        iconRes = R.drawable.ic_my,
        labelResId = R.string.navigation_profile
    )
}

private const val ROUTE_HOME = "home"
private const val ROUTE_VIDEO = "video"
private const val ROUTE_VIDEO_ADD = "videoAdd"
private const val ROUTE_PROFILE = "profile"