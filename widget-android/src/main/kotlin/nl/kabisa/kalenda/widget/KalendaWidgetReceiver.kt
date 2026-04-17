package nl.kabisa.kalenda.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class KalendaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = KalendaWidget()
}
