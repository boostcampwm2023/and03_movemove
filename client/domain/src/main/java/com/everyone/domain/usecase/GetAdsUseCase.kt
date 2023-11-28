package com.everyone.domain.usecase

import com.everyone.domain.model.Ads
import com.everyone.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAdsUseCase @Inject constructor(private val mainRepository: MainRepository) {
    suspend operator fun invoke(): Flow<Ads> = mainRepository.getAds()
}