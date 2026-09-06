package com.example.draw.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.imageDataStore by preferencesDataStore(
    name = "image_preferences"
)

class ImagePreferencesManager(
    private val context: Context
) {

    companion object {
        private val IS_FAVOURITE = booleanPreferencesKey("is_favourite")
    }

    suspend fun saveFavourite(value: Boolean) {
        context.imageDataStore.edit {
            it[IS_FAVOURITE] = value
        }
    }

    fun getFavourite(): Flow<Boolean> {
        return context.imageDataStore.data.map {
            it[IS_FAVOURITE] ?: false
        }
    }
}