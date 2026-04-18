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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

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
    enabled: Boolean,
    calendars: List<CalendarInfo>,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onToggleCalendar: (String) -> Unit,
    onToggleCalendarAllDay: (String) -> Unit,
) {
    val colors = LocalKalendaColors.current
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
                fontSize = 14.sp,
                modifier = Modifier.rotate(chevronRotation)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (enabled) color else Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = email.first().uppercase(),
                    color = Color(0xFF0B1220),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = email,
                    color = if (enabled) colors.textPrimary else colors.textMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (enabled) "$activeCals of ${calendars.size} calendar${if (calendars.size != 1) "s" else ""} active" else "Paused",
                    color = colors.textSubtle,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            KalendaSwitch(checked = enabled, onCheckedChange = { onToggle() }, accent = color)
            Spacer(modifier = Modifier.width(8.dp))
            var confirmRemove by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { confirmRemove = true },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val s = size.width / 10f
                    val stroke = 1.5f * s
                    drawLine(
                        Color(0xFF9E9E9E),
                        Offset(1.5f * s, 1.5f * s), Offset(8.5f * s, 8.5f * s),
                        stroke, StrokeCap.Round,
                    )
                    drawLine(
                        Color(0xFF9E9E9E),
                        Offset(8.5f * s, 1.5f * s), Offset(1.5f * s, 8.5f * s),
                        stroke, StrokeCap.Round,
                    )
                }
            }
            if (confirmRemove) {
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    title = { Text("Remove account?") },
                    text = { Text("$email will be removed from Kalenda. You can add it again later.") },
                    confirmButton = {
                        TextButton(onClick = {
                            confirmRemove = false
                            onRemove()
                        }) { Text("Remove") }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmRemove = false }) { Text("Cancel") }
                    },
                )
            }
        }

        AnimatedVisibility(visible = expanded && calendars.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(start = 38.dp, end = 10.dp, bottom = 6.dp)
                    .alpha(if (enabled) 1f else 0.5f)
            ) {
                calendars.forEach { cal ->
                    CalendarChildRow(
                        color = cal.color,
                        name = cal.name,
                        enabled = cal.enabled,
                        showAllDay = cal.showAllDay,
                        onToggle = { onToggleCalendar(cal.id) },
                        onToggleAllDay = { onToggleCalendarAllDay(cal.id) },
                        parentEnabled = enabled,
                    )
                }
            }
        }
    }
}
