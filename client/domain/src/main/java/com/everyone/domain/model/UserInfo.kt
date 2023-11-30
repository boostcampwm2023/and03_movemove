package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserInfo(
    val jsonWebToken: JsonWebToken?,
    val profile: Profile?
) : BaseModel
