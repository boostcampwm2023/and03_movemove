package com.everyone.domain.usecase

import com.everyone.domain.model.ProfileImageUploadUrl
import com.everyone.domain.model.base.DataState
import com.everyone.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileImageUploadUrlUseCase @Inject constructor(private val repository: UserRepository) {
    operator fun invoke(
        profileImageExtension: String,
        uuid: String,
        idToken: String
    ): Flow<DataState<ProfileImageUploadUrl>> = repository.getProfileImageUploadUrl(
        profileImageExtension = profileImageExtension,
        uuid = uuid,
        idToken = idToken
    )
}