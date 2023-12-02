package com.everyone.data.repository

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.remote.RemoteConstants.CATEGORY
import com.everyone.data.remote.RemoteConstants.CONTENT
import com.everyone.data.remote.RemoteConstants.LIMIT
import com.everyone.data.remote.RemoteConstants.PRESIGNED_URL
import com.everyone.data.remote.RemoteConstants.RANDOM
import com.everyone.data.remote.RemoteConstants.RATING
import com.everyone.data.remote.RemoteConstants.REASON
import com.everyone.data.remote.RemoteConstants.SEED
import com.everyone.data.remote.RemoteConstants.THUMBNAIL_EXTENSION
import com.everyone.data.remote.RemoteConstants.TITLE
import com.everyone.data.remote.RemoteConstants.TOP_RATED
import com.everyone.data.remote.RemoteConstants.TREND
import com.everyone.data.remote.RemoteConstants.VIDEO
import com.everyone.data.remote.RemoteConstants.VIDEOS
import com.everyone.data.remote.RemoteConstants.VIDEO_EXTENSION
import com.everyone.data.remote.RemoteConstants.VIEWS
import com.everyone.data.remote.model.CreatedVideoResponse
import com.everyone.data.remote.model.CreatedVideoResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoUploadUrlResponse
import com.everyone.data.remote.model.VideoUploadUrlResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosRandomResponse
import com.everyone.data.remote.model.VideosRandomResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosTrendResponse
import com.everyone.data.remote.model.VideosTrendResponse.Companion.toDomainModel
import com.everyone.domain.model.CreatedVideo
import com.everyone.domain.model.VideoUploadUrl
import com.everyone.domain.model.VideosRandom
import com.everyone.domain.model.VideosTrend
import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError
import com.everyone.domain.repository.VideosRepository
import io.ktor.http.HttpMethod
import io.ktor.http.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class VideosRepositoryImpl @Inject constructor(
    private val networkHandler: NetworkHandler
) : VideosRepository {

    override fun postExtensionInfo(
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<VideoUploadUrl>> = flow {
        networkHandler.request<VideoUploadUrlResponse>(
            method = HttpMethod.Get,
            url = {
                path(PRESIGNED_URL, VIDEO)
                parameters.append(VIDEO_EXTENSION, videoExtension)
                parameters.append(THUMBNAIL_EXTENSION, thumbnailExtension)
            }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override fun putFile(
        requestUrl: String,
        file: File
    ): Flow<Int> = flow {
        networkHandler.requestSendFileToNCP(
            url = requestUrl,
            file = file
        ).collect { statusCode ->
            emit(statusCode)
        }
    }

    override fun postVideoInfo(
        videoId: String,
        title: String,
        content: String,
        category: String,
        videoExtension: String,
        thumbnailExtension: String
    ): Flow<DataState<CreatedVideo>> = flow {
        networkHandler.request<CreatedVideoResponse>(
            method = HttpMethod.Post,
            url = { path(VIDEOS, videoId) },
            content = {
                append(TITLE, title)
                append(CONTENT, content)
                append(CATEGORY, category)
                append(VIDEO_EXTENSION, videoExtension)
                append(THUMBNAIL_EXTENSION, thumbnailExtension)
            }
        ).collect { response ->
            response.data?.let {
                emit(DataState.Success(it.toDomainModel()))
            } ?: run {
                emit(response.toFailure())
            }
        }
    }

    override suspend fun getVideosRandom(
        limit: String,
        category: String,
        seed: String
    ): Flow<DataState<VideosRandom>> {
        return flow {
            networkHandler.request<VideosRandomResponse>(
                method = HttpMethod.Get,
                url = {
                    path(VIDEOS, RANDOM)
                    parameters.append(LIMIT, limit)
                    parameters.append(CATEGORY, category)
                    parameters.append(SEED, seed)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override suspend fun putVideosRating(
        id: String,
        rating: String,
        reason: String
    ): Flow<DataState<Unit>> {
        return flow {
            networkHandler.request<Unit>(
                method = HttpMethod.Put,
                url = { path(VIDEOS, id, RATING) },
                content = {
                    append(RATING, rating)
                    append(REASON, reason)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override fun putVideosViews(videoId: String, seed: String): Flow<DataState<Unit>> =
        flow {
            networkHandler.request<Unit>(
                method = HttpMethod.Put,
                url = { path(VIDEOS, videoId, VIEWS) },
                content = { append(SEED, seed) }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }

    override suspend fun getVideosTopRated(category: String): Flow<DataState<VideosTrend>> {
        return flow {
            networkHandler.request<VideosTrendResponse>(
                method = HttpMethod.Get,
                url = {
                    path(VIDEOS, TOP_RATED)
                    parameters.append(CATEGORY, category)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }

    override suspend fun getVideosTrend(limit: String): Flow<DataState<VideosTrend>> {
        return flow {
            networkHandler.request<VideosTrendResponse>(
                method = HttpMethod.Get,
                url = {
                    path(VIDEOS, TREND)
                    parameters.append(LIMIT, limit)
                }
            ).collect { response ->
                response.data?.let {
                    emit(DataState.Success(it.toDomainModel()))
                } ?: run {
                    emit(DataState.Failure(NetworkError(response.statusCode, response.message)))
                }
            }
        }
    }
}