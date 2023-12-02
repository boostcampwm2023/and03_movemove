package com.everyone.movemove_android.di

import com.everyone.domain.repository.UserRepository
import com.everyone.domain.repository.VideosRepository
import com.everyone.domain.usecase.GetStoredSignedPlatformUseCase
import com.everyone.domain.usecase.GetStoredUserIdUseCase
import com.everyone.domain.usecase.PostExtensionInfoUseCase
import com.everyone.domain.usecase.PostVideoInfoUseCase
import com.everyone.domain.usecase.PutFileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    /*
    유저
     */
    @Provides
    @ViewModelScoped
    fun provideGetStoredUserIdUseCase(repository: UserRepository): GetStoredUserIdUseCase = GetStoredUserIdUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetStoredSignedPlatformUseCase(repository: UserRepository): GetStoredSignedPlatformUseCase = GetStoredSignedPlatformUseCase(repository)

    /*
    비디오
     */
    @Provides
    @ViewModelScoped
    fun providePostExtensionInfoUseCase(repository: VideosRepository): PostExtensionInfoUseCase = PostExtensionInfoUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providePutFileUseCase(repository: VideosRepository): PutFileUseCase = PutFileUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providePostVideoInfoUseCase(repository: VideosRepository): PostVideoInfoUseCase = PostVideoInfoUseCase(repository)
}