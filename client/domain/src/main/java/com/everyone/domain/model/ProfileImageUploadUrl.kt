package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileImageUploadUrl(
    val presignedUrl: String?
) : BaseModel
