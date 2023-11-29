package com.everyone.movemove_android.di

import com.everyone.data.remote.NetworkHandler
import com.everyone.data.repository.AdsRepositoryImpl
import com.everyone.data.repository.MainRepositoryImpl
import com.everyone.domain.repository.AdsRepository
import com.everyone.domain.repository.MainRepository
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
    fun provideMainRepository(networkHandler: NetworkHandler): MainRepository {
        return MainRepositoryImpl(networkHandler)
    }

    @Provides
    @Singleton
    fun provideAdsRepository(networkHandler: NetworkHandler): AdsRepository {
        return AdsRepositoryImpl(networkHandler)
    }
}