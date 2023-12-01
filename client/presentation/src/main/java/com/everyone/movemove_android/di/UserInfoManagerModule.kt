package com.everyone.movemove_android.di

import android.content.Context
import com.everyone.data.local.UserInfoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserInfoManagerModule {

    @Provides
    @Singleton
    fun provideUserInfoManager(@ApplicationContext context: Context): UserInfoManager = UserInfoManager(context)
}