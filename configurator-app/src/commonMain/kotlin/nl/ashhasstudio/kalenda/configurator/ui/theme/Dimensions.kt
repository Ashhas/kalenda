package nl.ashhasstudio.kalenda.configurator.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design-token system. Replaces ad-hoc .dp/.sp literals scattered across components.
 * When something here doesn't fit a component, prefer adjusting the token or accepting
 * a one-off literal inside the component — don't duplicate these.
 */

object Spacing {
    val screenPadding = 12.dp
    val cardHorizontalPadding = 18.dp
    val sectionLeftPadding = 17.dp
    val rowHorizontalPadding = 14.dp
    val rowVerticalPadding = 9.dp
    val elementGap = 12.dp
    val itemGap = 10.dp
    val tightGap = 4.dp
    val scrollableBottomPadding = 24.dp
}

object Shapes {
    val cardRadius = 16.dp
    val buttonRadius = 14.dp
    val smallRadius = 8.dp
    val tinyRadius = 6.dp
    val pillRadius = 4.dp
    val checkboxRadius = 2.dp
}

object FontSizes {
    val heroNumber = 34.sp
    val title = 22.sp
    val primary = 15.sp
    val body = 14.sp
    val subtle = 13.sp
    val small = 12.sp
    val tiny = 11.sp
}

object Sizes {
    val colorSwatch = 28.dp
    val navIconContainer = 32.dp
    val smallIcon = 20.dp
    val toggleBarHeight = 30.dp
    val eventPillBarHeight = 16.dp
    val eventPillBarWidth = 3.dp
    val accountAvatar = 24.dp
    val closeButton = 24.dp
}
