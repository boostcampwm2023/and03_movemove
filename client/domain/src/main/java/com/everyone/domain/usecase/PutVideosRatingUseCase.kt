package com.everyone.domain.usecase

import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PutVideosRatingUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        id: String,
        rating: String,
        reason: String
    ): Flow<DataState<Unit>> {
        return repository.putVideosRating(
            id = id,
            rating = rating,
            reason = reason
        )
    }
}