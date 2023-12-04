package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRatedItem(
    val video: Video?,
    val uploader: Uploader?,
    val ratedAt : String?
) : BaseModel