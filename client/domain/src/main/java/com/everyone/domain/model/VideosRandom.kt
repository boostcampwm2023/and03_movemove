package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideosRandom(
    val videos: List<Videos>?
) : BaseModel
