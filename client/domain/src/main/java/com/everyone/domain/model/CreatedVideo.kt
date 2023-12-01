package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatedVideo(
    val id: String?,
    val title: String?,
    val content: String?,
    val category: String?
) : BaseModel
