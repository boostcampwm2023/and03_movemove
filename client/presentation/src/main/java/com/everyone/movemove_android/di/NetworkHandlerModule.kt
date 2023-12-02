package com.everyone.movemove_android.di

import com.everyone.data.local.UserInfoManager
import com.everyone.data.remote.NetworkHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkHandlerModule {

    @Provides
    @Singleton
    fun provideNetworkHandler(userInfoManager: UserInfoManager): NetworkHandler = NetworkHandler(userInfoManager)
}