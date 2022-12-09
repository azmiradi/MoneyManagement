package com.demo.preferences.general

import kotlinx.coroutines.flow.Flow

interface GeneralPrefsStore {
    fun getID(): Flow<String>
    suspend fun saveID(token:String)
    suspend fun clearData( )

}