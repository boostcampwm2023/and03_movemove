package com.everyone.domain.usecase

import com.everyone.domain.model.ProfileImageUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPresignedUrlProfileUseCase @Inject constructor(private val repository: UserRepository)  {
    operator fun invoke(profileExtension: String): Flow<DataState<ProfileImageUploadUrl>> = repository.getPresignedUrlProfile(profileExtension = profileExtension)
}