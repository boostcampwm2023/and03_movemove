package com.everyone.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInfoManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.instance: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

    /*
    다른 타입의 로컬 저장이 필요한 경우, 오버로딩 해주세요.
     */

    suspend fun store(
        key: String,
        value: String
    ): Flow<Boolean> = flow {
        runCatching {
            context.instance.edit {
                it[stringPreferencesKey(key)] = value
            }
        }.onSuccess {
            emit(true)
        }.onFailure {
            emit(false)
        }
    }

    fun load(key: String): Flow<String?> = context.instance.data.map {
        it[stringPreferencesKey(key)]
    }

    companion object {
        private const val DATASTORE_NAME = "movemove_datastore"
        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
        const val KEY_USER_ID = "key_user_id"
    }
}