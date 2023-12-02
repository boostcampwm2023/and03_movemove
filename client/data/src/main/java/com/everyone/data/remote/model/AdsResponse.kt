package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Ads
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdsResponse(
    val name: String?,
    val url: String?
) : BaseResponse {
    companion object : Mapper<AdsResponse, Ads> {
        override fun AdsResponse.toDomainModel(): Ads {
            return Ads(
                name = name,
                url = url
            )
        }
    }
}