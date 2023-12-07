package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Rater
import kotlinx.parcelize.Parcelize

@Parcelize
data class RaterResponse(
    val uuid: String?,
    val nickname: String?,
) : BaseResponse {
    companion object : Mapper<RaterResponse, Rater> {
        override fun RaterResponse.toDomainModel(): Rater {
            return Rater(
                uuid = uuid,
                nickname = nickname,
            )
        }
    }
}
