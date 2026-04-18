package nl.ashhasstudio.kalenda.configurator.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.AllDayPosition
import nl.ashhasstudio.kalenda.domain.WidgetSettings

@Composable
fun WidgetSettingsScreen(
    settingsRepository: SettingsRepository,
    onSettingsChanged: () -> Unit = {}
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    var sliderValue by remember(settings.scrollDays) { mutableFloatStateOf(settings.scrollDays.toFloat()) }

    Column(modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(16.dp)) {
        Text("Widget Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Scroll range: ${sliderValue.toInt()} days")
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                scope.launch {
                    settingsRepository.updateSettings(settings.copy(scrollDays = sliderValue.toInt()))
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

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onSettingsChanged() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply to Widget")
        }
    }
}
