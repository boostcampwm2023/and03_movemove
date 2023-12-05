package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideosList(
    val videos: List<Videos>?
) : BaseModel
