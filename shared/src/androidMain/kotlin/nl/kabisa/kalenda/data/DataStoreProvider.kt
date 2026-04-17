package nl.kabisa.kalenda.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kalenda_prefs")

object DataStoreProvider {
    private var instance: DataStore<Preferences>? = null

    fun get(context: Context): DataStore<Preferences> {
        return instance ?: context.applicationContext.dataStore.also { instance = it }
    }
}
