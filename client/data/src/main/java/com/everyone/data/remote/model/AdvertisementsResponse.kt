package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Ads
import com.everyone.domain.model.Advertisements
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdvertisementsResponse(
    val advertisements: List<Ads>?
) : BaseResponse {
    companion object : Mapper<AdvertisementsResponse, Advertisements> {
        override fun AdvertisementsResponse.toDomainModel(): Advertisements {
            return Advertisements(
                advertisements = advertisements
            )
        }
    }
}