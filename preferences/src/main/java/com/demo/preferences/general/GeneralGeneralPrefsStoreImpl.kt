package com.demo.preferences.general

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class GeneralGeneralPrefsStoreImpl @Inject constructor(
    private val datastore: DataStore<Preferences>
) : GeneralPrefsStore {

    companion object {
        val dataSaved = stringPreferencesKey("dataSaved")
    }

    override fun getID(): Flow<String> = datastore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map {
        it[dataSaved] ?: ""
    }

    override suspend fun saveID(token: String) {
        datastore.edit {
            it[dataSaved] = token
        }
    }

    override suspend fun clearData( ) {
        datastore.edit {
            it.clear()
        }
    }


}