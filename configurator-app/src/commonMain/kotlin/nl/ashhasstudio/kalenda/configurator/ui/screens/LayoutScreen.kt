package nl.ashhasstudio.kalenda.configurator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.ui.components.DayStepper
import nl.ashhasstudio.kalenda.configurator.ui.components.SegmentOption
import nl.ashhasstudio.kalenda.configurator.ui.components.SegmentedControl
import nl.ashhasstudio.kalenda.configurator.ui.components.SubBar
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetDivider
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetSectionHeader
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.AllDayPosition
import nl.ashhasstudio.kalenda.domain.DayMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings

@Composable
fun LayoutScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    val colors = LocalKalendaColors.current
    val accent = accentColorForHue(settings.accentHue)

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val daysLeftThisWeek = 8 - (today.dayOfWeek.ordinal + 1)
    val headerNumber: Int
    val headerLabel: String
    val headerSubtitle: String
    when (settings.dayMode) {
        DayMode.THIS_WEEK -> {
            headerNumber = daysLeftThisWeek
            headerLabel = "days left"
            headerSubtitle = "Shows only the remaining days of this week (Mon–Sun)"
        }
        DayMode.ROLLING -> {
            headerNumber = settings.scrollDays
            headerLabel = "days visible"
            headerSubtitle = "How many days of events the widget shows"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        SubBar(title = "Layout", onBack = onBack)

        WidgetCard {
            Column(modifier = Modifier.padding(start = 17.dp, end = 17.dp, top = 18.dp, bottom = 8.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = headerNumber.toString(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.5).sp,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = headerLabel,
                        color = accent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Text(
                    text = headerSubtitle,
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            WidgetSectionHeader(label = "Date range", topSpace = 6)
            SegmentedControl(
                options = listOf(
                    SegmentOption(DayMode.ROLLING, "Rolling days"),
                    SegmentOption(DayMode.THIS_WEEK, "This week"),
                ),
                selected = settings.dayMode,
                onSelect = { mode ->
                    scope.launch {
                        settingsRepository.updateSettings(settings.copy(dayMode = mode))
                    }
                },
            )

            when (settings.dayMode) {
                DayMode.ROLLING -> DayStepper(
                    value = settings.scrollDays,
                    onValueChange = { days ->
                        scope.launch {
                            settingsRepository.updateSettings(settings.copy(scrollDays = days))
                        }
                    },
                    accent = accent,
                )
                DayMode.THIS_WEEK -> Text(
                    text = "Week starts on Monday. The widget shows today plus the rest of this week — at the start of a new week it refills automatically.",
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 14.dp),
                )
            }

            WidgetDivider()

            WidgetSectionHeader(label = "All-day events")
            SegmentedControl(
                options = listOf(
                    SegmentOption(AllDayPosition.TOP, "Top"),
                    SegmentOption(AllDayPosition.BOTTOM, "Bottom"),
                    SegmentOption(AllDayPosition.HIDDEN, "Hidden"),
                ),
                selected = settings.allDayPosition,
                onSelect = { pos ->
                    scope.launch {
                        settingsRepository.updateSettings(settings.copy(allDayPosition = pos))
                    }
                },
            )
        }
    }
}
