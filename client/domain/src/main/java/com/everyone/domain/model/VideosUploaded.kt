package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideosUploaded(
    val uploader: Uploader?,
    val videos: List<Video>?,
) : BaseModel
