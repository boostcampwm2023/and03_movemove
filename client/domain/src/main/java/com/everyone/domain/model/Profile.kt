package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    val uuid: String? = null,
    val nickname: String? = null,
    val statusMessage: String? = null,
    val profileImageUrl: String? = null
) : BaseModel
