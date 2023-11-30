package com.everyone.domain.usecase

import com.everyone.domain.repository.VideosRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class PutFileUseCase @Inject constructor(private val repository: VideosRepository) {
    operator fun invoke(
        requestUrl: String,
        file: File
    ): Flow<Int> = repository.putFile(
        requestUrl = requestUrl,
        file = file
    )
}