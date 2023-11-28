package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.model.UrlParamsBuilder
import com.everyone.data.remote.model.VideosRandomResponse
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


    companion object {
        const val GET_VIDEOS_RANDOM_PATH = "/videos/random"
        const val GET_VIDEOS_RANDOM_LIMIT = "limit"
        const val GET_VIDEOS_RANDOM_CATEGORY = "category"
    }
}