package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Video
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class VideoResponse(
    val _id: String?,
    val viewCount: Int?,
    val rating: String?,
    val category: String?,
    val title: String?,
    val content: String?,
    val uploadedAt: Date?,
    val manifest: String?,
    val thumbnailImage: String?
) : BaseResponse {
    companion object : Mapper<VideoResponse, Video> {
        override fun VideoResponse.toDomainModel(): Video {
            return Video(
                id = _id,
                viewCount = viewCount,
                rating = rating,
                category = category,
                title = title,
                content = content,
                uploadedAt = uploadedAt,
                manifest = manifest,
                thumbnailImage = thumbnailImage
            )
        }
    }
}
