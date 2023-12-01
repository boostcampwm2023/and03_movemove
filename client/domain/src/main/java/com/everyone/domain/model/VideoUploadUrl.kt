package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUploadUrl(
    val videoId: String? = "",
    val videoUrl: String? = "",
    val thumbnailUrl: String? = ""
) : BaseModel
