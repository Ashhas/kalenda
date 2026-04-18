package nl.ashhasstudio.kalenda.configurator

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import nl.ashhasstudio.kalenda.widget.KalendaWidget

private val LAST_UPDATE_KEY = longPreferencesKey("last_update")

suspend fun refreshWidget(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(KalendaWidget::class.java)
    Log.d("KalendaRefresh", "Found ${glanceIds.size} widget instances, updating...")
    val widget = KalendaWidget()
    for (id in glanceIds) {
        updateAppWidgetState(context, id) { prefs ->
            prefs[LAST_UPDATE_KEY] = System.currentTimeMillis()
        }
        widget.update(context, id)
    }
    Log.d("KalendaRefresh", "Widget update complete")
}
