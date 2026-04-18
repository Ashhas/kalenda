package nl.ashhasstudio.kalenda.configurator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.ui.components.AccountRow
import nl.ashhasstudio.kalenda.configurator.ui.components.AddAccountRow
import nl.ashhasstudio.kalenda.configurator.ui.components.CalendarInfo
import nl.ashhasstudio.kalenda.configurator.ui.components.SubBar
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetSectionHeader
import nl.ashhasstudio.kalenda.configurator.ui.theme.CalColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.WidgetSettings

@Composable
fun CalendarsScreen(
    settingsRepository: SettingsRepository,
    calendarRepository: CalendarRepository,
    onAddAccount: () -> Unit,
    onCalendarsChanged: () -> Unit,
    onBack: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    val colors = LocalKalendaColors.current
    val accent = accentColorForHue(settings.accentHue)

    val accountColors = listOf(
        CalColors.peacock, CalColors.flamingo, CalColors.lavender,
        CalColors.basil, CalColors.banana, CalColors.tomato,
    )

    LaunchedEffect(settings.accounts.map { it.id }) {
        var fetchedAny = false
        for (account in settings.accounts) {
            if (account.calendars.isEmpty()) {
                try {
                    val calendars = calendarRepository.fetchCalendarList(account)
                    val updated = account.copy(calendars = calendars)
                    settingsRepository.updateAccount(updated)
                    fetchedAny = true
                } catch (_: Exception) { }
            }
        }
        if (fetchedAny) onCalendarsChanged()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        SubBar(title = "Calendars", onBack = onBack)

        WidgetCard {
            WidgetSectionHeader(
                label = "${settings.accounts.size} connected",
                topSpace = 12
            )

            settings.accounts.forEachIndexed { index, account ->
                val color = accountColors[index % accountColors.size]

                val calendarInfos = account.calendars.map { cal ->
                    CalendarInfo(
                        id = cal.id,
                        name = cal.name,
                        color = Color(cal.color),
                        enabled = cal.enabled,
                        showAllDay = cal.showAllDay,
                    )
                }

                AccountRow(
                    color = color,
                    email = account.email,
                    enabled = true,
                    calendars = calendarInfos,
                    onToggle = { },
                    onRemove = {
                        scope.launch {
                            settingsRepository.removeAccount(account.id)
                            onCalendarsChanged()
                        }
                    },
                    onToggleCalendar = { calId ->
                        val cal = account.calendars.find { it.id == calId } ?: return@AccountRow
                        scope.launch {
                            settingsRepository.updateCalendarEnabled(account.id, calId, !cal.enabled)
                            onCalendarsChanged()
                        }
                    },
                    onToggleCalendarAllDay = { calId ->
                        val cal = account.calendars.find { it.id == calId } ?: return@AccountRow
                        scope.launch {
                            settingsRepository.updateCalendarShowAllDay(account.id, calId, !cal.showAllDay)
                            onCalendarsChanged()
                        }
                    },
                )
            }

            AddAccountRow(onClick = onAddAccount)
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExplainerCard(accent = accent)
    }
}

@Composable
private fun ExplainerCard(accent: Color) {
    val colors = LocalKalendaColors.current
    WidgetCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "What can I do here?",
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accent)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tap a calendar row",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = "Show or hide that calendar in the widget",
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
                Canvas(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(14.dp)
                ) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val r = size.width * 0.17f
                    val rayLen = size.width * 0.11f
                    val rayDist = size.width * 0.3f
                    val stroke = Stroke(width = size.width * 0.1f, cap = StrokeCap.Round)
                    drawCircle(accent, r, style = stroke)
                    listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f).forEach { angle ->
                        val rad = Math.toRadians(angle.toDouble())
                        val cos = kotlin.math.cos(rad).toFloat()
                        val sin = kotlin.math.sin(rad).toFloat()
                        drawLine(
                            accent,
                            Offset(cx + cos * rayDist, cy - sin * rayDist),
                            Offset(cx + cos * (rayDist + rayLen), cy - sin * (rayDist + rayLen)),
                            stroke.width, StrokeCap.Round,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tap the sun icon",
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                    )
                    Text(
                        text = "Hide all-day events for that calendar while keeping timed events visible. Useful for busy holiday calendars that clutter your view.",
                        color = colors.textMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 1.dp),
                    )
                }
            }
        }
    }
}
