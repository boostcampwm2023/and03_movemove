package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Video(
    val id: String?,
    val viewCount: Int?,
    val rating: String?,
    val category: String?,
    val title: String?,
    val content: String?,
    val uploadedAt: Date?,
    val manifest: String?,
    val thumbnailImageUrl: String?
) : BaseModel
