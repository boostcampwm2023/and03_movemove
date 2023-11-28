package com.everyone.domain.usecase

import com.everyone.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PutVideosRatingUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(
        id: String,
        rating: String,
        reason: String
    ): Flow<Unit> {
        return mainRepository.putVideosRating(
            id = id,
            rating = rating,
            reason = reason
        )
    }
}