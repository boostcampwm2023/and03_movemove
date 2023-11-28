package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Uploader(
    val uuid: String,
    val nickname: String,
    val statusMessage: String,
    val profileImage: String
) : BaseModel
