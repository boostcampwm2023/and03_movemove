package com.everyone.movemove_android.di

import com.everyone.domain.repository.VideosRepository
import com.everyone.domain.usecase.PostExtensionInfoUseCase
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
    비디오
     */
    @Provides
    @ViewModelScoped
    fun providePostExtensionInfoUseCase(repository: VideosRepository): PostExtensionInfoUseCase = PostExtensionInfoUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideFileUseCase(repository: VideosRepository): PutFileUseCase = PutFileUseCase(repository)
}