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
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Spacing
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.AllDayPosition
import nl.ashhasstudio.kalenda.domain.DayMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import nl.ashhasstudio.kalenda.usecase.daysLeftInWeekInclusive

@Composable
fun LayoutScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    val accent = accentColorForHue(settings.accentHue)

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val daysLeftThisWeek = daysLeftInWeekInclusive(today)
    val headerNumber: Int
    val headerLabel: String
    val headerSubtitle: String
    when (settings.dayMode) {
        DayMode.THIS_WEEK -> {
            headerNumber = daysLeftThisWeek
            headerLabel = strings.layoutHeaderDaysLeft
            headerSubtitle = strings.layoutSubtitleThisWeek
        }
        DayMode.ROLLING -> {
            headerNumber = settings.scrollDays
            headerLabel = strings.layoutHeaderDaysVisible
            headerSubtitle = strings.layoutSubtitleRolling
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.screenPadding)
    ) {
        SubBar(title = strings.layoutTitle, onBack = onBack)

        WidgetCard {
            Column(
                modifier = Modifier.padding(
                    start = Spacing.sectionLeftPadding,
                    end = Spacing.sectionLeftPadding,
                    top = 18.dp,
                    bottom = 8.dp,
                )
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = headerNumber.toString(),
                        color = colors.textPrimary,
                        fontSize = FontSizes.heroNumber,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        letterSpacing = (-0.5).sp,
                    )
                    Spacer(modifier = Modifier.width(Spacing.itemGap))
                    Text(
                        text = headerLabel,
                        color = accent,
                        fontSize = FontSizes.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = Spacing.tightGap),
                    )
                }
                Text(
                    text = headerSubtitle,
                    color = colors.textMuted,
                    fontSize = FontSizes.small,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            WidgetSectionHeader(label = strings.layoutDateRange, topSpace = 6)
            SegmentedControl(
                options = listOf(
                    SegmentOption(DayMode.ROLLING, strings.layoutOptionRolling),
                    SegmentOption(DayMode.THIS_WEEK, strings.layoutOptionThisWeek),
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
                    text = strings.layoutThisWeekHint,
                    color = colors.textMuted,
                    fontSize = FontSizes.small,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(
                        start = Spacing.cardHorizontalPadding,
                        end = Spacing.cardHorizontalPadding,
                        top = Spacing.tightGap,
                        bottom = 14.dp,
                    ),
                )
            }

            WidgetDivider()

            WidgetSectionHeader(label = strings.layoutAllDay)
            SegmentedControl(
                options = listOf(
                    SegmentOption(AllDayPosition.TOP, strings.allDayOptionTop),
                    SegmentOption(AllDayPosition.BOTTOM, strings.allDayOptionBottom),
                    SegmentOption(AllDayPosition.HIDDEN, strings.allDayOptionHidden),
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
