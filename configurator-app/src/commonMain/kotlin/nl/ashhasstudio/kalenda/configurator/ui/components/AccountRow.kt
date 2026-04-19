package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Sizes

private val AvatarTextColor = Color(0xFF0B1220)

data class CalendarInfo(
    val id: String,
    val name: String,
    val color: Color,
    val enabled: Boolean,
    val showAllDay: Boolean = true,
)

@Composable
fun AccountRow(
    color: Color,
    email: String,
    calendars: List<CalendarInfo>,
    onRemove: () -> Unit,
    onToggleCalendar: (String) -> Unit,
    onToggleCalendarAllDay: (String) -> Unit,
) {
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    var expanded by remember { mutableStateOf(true) }
    val chevronRotation by animateFloatAsState(if (expanded) 90f else 0f)
    val activeCals = calendars.count { it.enabled }

    Column {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "›",
                color = colors.textMuted,
                fontSize = FontSizes.body,
                modifier = Modifier.rotate(chevronRotation)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(Sizes.accountAvatar)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = email.first().uppercase(),
                    color = AvatarTextColor,
                    fontSize = FontSizes.tiny,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = email,
                    color = colors.textPrimary,
                    fontSize = FontSizes.body,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = strings.calendarsActiveSummary(activeCals, calendars.size),
                    color = colors.textSubtle,
                    fontSize = FontSizes.tiny,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            var confirmRemove by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(Sizes.closeButton)
                    .clip(CircleShape)
                    .clickable { confirmRemove = true },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val s = size.width / 10f
                    val stroke = 1.5f * s
                    drawLine(
                        colors.textSubtle,
                        Offset(1.5f * s, 1.5f * s), Offset(8.5f * s, 8.5f * s),
                        stroke, StrokeCap.Round,
                    )
                    drawLine(
                        colors.textSubtle,
                        Offset(8.5f * s, 1.5f * s), Offset(1.5f * s, 8.5f * s),
                        stroke, StrokeCap.Round,
                    )
                }
            }
            if (confirmRemove) {
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    title = { Text(strings.accountRemoveTitle) },
                    text = { Text(strings.accountRemoveBody(email)) },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmRemove = false
                            onRemove()
                        }) { Text(strings.accountRemoveConfirm) }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmRemove = false }) { Text(strings.accountRemoveCancel) }
                    },
                )
            }
        }

        AnimatedVisibility(visible = expanded && calendars.isNotEmpty()) {
            Column(modifier = Modifier.padding(start = 38.dp, end = 10.dp, bottom = 6.dp)) {
                calendars.forEach { cal ->
                    CalendarChildRow(
                        color = cal.color,
                        name = cal.name,
                        enabled = cal.enabled,
                        showAllDay = cal.showAllDay,
                        onToggle = { onToggleCalendar(cal.id) },
                        onToggleAllDay = { onToggleCalendarAllDay(cal.id) },
                        parentEnabled = true,
                    )
                }
            }
        }
    }
}
