package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ads(
    val name: String?,
    val url: String?
) : BaseModel
