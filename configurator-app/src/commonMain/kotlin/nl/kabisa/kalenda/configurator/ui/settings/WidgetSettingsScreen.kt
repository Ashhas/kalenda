package nl.kabisa.kalenda.configurator.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.kabisa.kalenda.data.SettingsRepository
import nl.kabisa.kalenda.domain.AllDayPosition
import nl.kabisa.kalenda.domain.WidgetSettings

@Composable
fun WidgetSettingsScreen(settingsRepository: SettingsRepository) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Widget Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Scroll range: ${settings.scrollDays} days")
        Slider(
            value = settings.scrollDays.toFloat(),
            onValueChange = { newValue ->
                scope.launch {
                    settingsRepository.updateSettings(settings.copy(scrollDays = newValue.toInt()))
                }
            },
            valueRange = 1f..14f,
            steps = 12
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("All-day events")
        AllDayPosition.entries.forEach { position ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = settings.allDayPosition == position,
                    onClick = {
                        scope.launch {
                            settingsRepository.updateSettings(settings.copy(allDayPosition = position))
                        }
                    }
                )
                Text(position.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }
    }
}
