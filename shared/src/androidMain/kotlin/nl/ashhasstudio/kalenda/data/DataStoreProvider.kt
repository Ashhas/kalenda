package nl.ashhasstudio.kalenda.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// The `preferencesDataStore` delegate already ensures a single DataStore per Context per
// filename, so we don't need an extra `object DataStoreProvider` wrapper with DCL.
internal val Context.kalendaDataStore: DataStore<Preferences> by preferencesDataStore(name = "kalenda_prefs")

// Kept as a thin wrapper for the existing call sites; resolves to the same singleton.
object DataStoreProvider {
    fun get(context: Context): DataStore<Preferences> = context.applicationContext.kalendaDataStore
}
