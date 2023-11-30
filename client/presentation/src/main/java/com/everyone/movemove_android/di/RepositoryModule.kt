package com.everyone.movemove_android.di

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.repository.AdsRepositoryImpl
import com.everyone.data.repository.StartingRepositoryImpl
import com.everyone.data.repository.VideosRepositoryImpl
import com.everyone.domain.repository.AdsRepository
import com.everyone.domain.repository.StartingRepository
import com.everyone.domain.repository.VideosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMainRepository(networkHandler: NetworkHandler): VideosRepository {
        return VideosRepositoryImpl(networkHandler)
    }

    @Provides
    @Singleton
    fun provideAdsRepository(networkHandler: NetworkHandler): AdsRepository {
        return AdsRepositoryImpl(networkHandler)
    }

    @Provides
    @Singleton
    fun provideStartingRepository(networkHandler: NetworkHandler): StartingRepository {
        return StartingRepositoryImpl(networkHandler)
    }
}