package nl.ashhasstudio.kalenda.configurator

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import nl.ashhasstudio.kalenda.widget.KalendaWidget

suspend fun refreshWidget(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(KalendaWidget::class.java)
    Log.d("KalendaRefresh", "Found ${glanceIds.size} widget instances, updating...")
    val widget = KalendaWidget()
    for (id in glanceIds) {
        widget.update(context, id)
    }
    Log.d("KalendaRefresh", "Widget update complete")
}
