package com.everyone.data.mapper

import com.everyone.data.base.BaseResponse
import com.everyone.domain.model.base.BaseModel

interface Mapper<in R : BaseResponse, out D : BaseModel> {
    fun R.toDomainModel(): D
}