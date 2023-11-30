package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Videos(
    val video: Video?,
    val uploader: Uploader?
) : BaseModel
