package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JsonWebToken(
    val accessToken: String?,
    val refreshToken: String?
) : BaseModel
