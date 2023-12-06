package com.everyone.domain.model

import com.everyone.domain.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rater(
    val uuid: String?,
    val nickname: String?,
) : BaseModel
