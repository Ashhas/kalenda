package nl.kabisa.kalenda.configurator.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import nl.kabisa.kalenda.configurator.ui.preview.WidgetPreviewCard
import nl.kabisa.kalenda.data.CalendarRepository
import nl.kabisa.kalenda.data.SettingsRepository
import nl.kabisa.kalenda.domain.DayGroup
import nl.kabisa.kalenda.domain.WidgetSettings
import nl.kabisa.kalenda.usecase.GroupEventsByDayUseCase

@Composable
fun HomeScreen(
    settingsRepository: SettingsRepository,
    calendarRepository: CalendarRepository,
    onNavigateToAccounts: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val groupUseCase = remember { GroupEventsByDayUseCase() }
    var dayGroups by remember { mutableStateOf(emptyList<DayGroup>()) }
    val deviceTz = TimeZone.currentSystemDefault()

    LaunchedEffect(settings) {
        val cache = calendarRepository.getCachedEvents()
        dayGroups = groupUseCase(cache.events, settings, Clock.System.now(), deviceTz)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Kalenda", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        WidgetPreviewCard(dayGroups = dayGroups, deviceTimezone = deviceTz)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onNavigateToAccounts, modifier = Modifier.fillMaxWidth()) {
            Text("Manage Accounts")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onNavigateToSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Widget Settings")
        }
    }
}
