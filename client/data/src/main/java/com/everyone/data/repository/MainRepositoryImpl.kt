package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.model.AdsResponse
import com.everyone.data.remote.model.UrlParamsBuilder
import com.everyone.data.remote.model.VideosRandomResponse
import com.everyone.domain.model.Ads
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.repository.MainRepository
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val newtWorkHandler: NetworkHandler,
    private val urlParamsBuilder: UrlParamsBuilder = UrlParamsBuilder()
) : MainRepository {

    override suspend fun getAds(): Flow<Ads> {
        return flow {
            newtWorkHandler.request<AdsResponse>(
                method = HttpMethod.Get,
                urlParams = urlParamsBuilder.addPaths(GET_ADS_PATH).build(),
                content = null
            ).collect()
        }
    }

    override suspend fun getVideosRandom(
        limit: String,
        category: String,
    ): Flow<VideosRandom> {
        return flow {
            newtWorkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Get,
                urlParams = urlParamsBuilder.addPaths(GET_VIDEOS_RANDOM_PATH).addQueries(
                    GET_VIDEOS_RANDOM_LIMIT to limit,
                    GET_VIDEOS_RANDOM_CATEGORY to category
                ).build(),
                content = null
            ).collect()
        }
    }

    override suspend fun putVideosRating(
        id: String,
        rating: String,
        reason: String
    ): Flow<Unit> {
        return flow {
            newtWorkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Put,
                urlParams = urlParamsBuilder.addPaths(PUT_VIDEOS_RATING_PATH.format(id)).build(),
            ) {
                append(PUT_VIDEOS_RATING_RATING, rating)
                append(PUT_VIDEOS_RATING_REASON, reason)
            }.collect()
        }
    }

    companion object {
        const val GET_ADS_PATH = "/ads"

        const val GET_VIDEOS_RANDOM_PATH = "/videos/random"
        const val GET_VIDEOS_RANDOM_LIMIT = "limit"
        const val GET_VIDEOS_RANDOM_CATEGORY = "category"

        const val PUT_VIDEOS_RATING_PATH = "/videos/%s/rating"
        const val PUT_VIDEOS_RATING_RATING = "rating"
        const val PUT_VIDEOS_RATING_REASON = "reason"
    }
}