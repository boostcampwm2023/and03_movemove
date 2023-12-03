package com.everyone.movemove_android.di

import com.everyone.domain.repository.UserRepository
import com.everyone.domain.repository.VideosRepository
import com.everyone.domain.usecase.GetProfileImageUploadUrlUseCase
import com.everyone.domain.usecase.GetStoredSignedPlatformUseCase
import com.everyone.domain.usecase.GetStoredUUIDUseCase
import com.everyone.domain.usecase.GetVideoUploadUrlUseCase
import com.everyone.domain.usecase.LoginUseCase
import com.everyone.domain.usecase.PostVideoInfoUseCase
import com.everyone.domain.usecase.PutFileUseCase
import com.everyone.domain.usecase.SetAccessTokenUseCase
import com.everyone.domain.usecase.SignUpUseCase
import com.everyone.domain.usecase.StoreRefreshTokenUseCase
import com.everyone.domain.usecase.StoreSignedPlatformUseCase
import com.everyone.domain.usecase.StoreUUIDUseCase
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
    fun provideSignUpUseCase(repository: UserRepository): SignUpUseCase = SignUpUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(repository: UserRepository): LoginUseCase = LoginUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetStoredUUIDUseCase(repository: UserRepository): GetStoredUUIDUseCase = GetStoredUUIDUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetStoredSignedPlatformUseCase(repository: UserRepository): GetStoredSignedPlatformUseCase = GetStoredSignedPlatformUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetProfileImageUploadUrlUseCase(repository: UserRepository): GetProfileImageUploadUrlUseCase = GetProfileImageUploadUrlUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSetAccessTokenUseCase(repository: UserRepository): SetAccessTokenUseCase = SetAccessTokenUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideStoreRefreshTokenUseCase(repository: UserRepository): StoreRefreshTokenUseCase = StoreRefreshTokenUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideStoreUUIDUseCase(repository: UserRepository): StoreUUIDUseCase = StoreUUIDUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSignedPlatformUseCase(repository: UserRepository): StoreSignedPlatformUseCase = StoreSignedPlatformUseCase(repository)

    /*
    비디오
     */
    @Provides
    @ViewModelScoped
    fun provideGetVideoUploadUrlUseCase(repository: VideosRepository): GetVideoUploadUrlUseCase = GetVideoUploadUrlUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providePutFileUseCase(repository: VideosRepository): PutFileUseCase = PutFileUseCase(repository)

    @Provides
    @ViewModelScoped
    fun providePostVideoInfoUseCase(repository: VideosRepository): PostVideoInfoUseCase = PostVideoInfoUseCase(repository)
}