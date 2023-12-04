package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRated(
    val rater: Rater?,
    val videos: List<VideosRatedItem>?,
) : BaseModel