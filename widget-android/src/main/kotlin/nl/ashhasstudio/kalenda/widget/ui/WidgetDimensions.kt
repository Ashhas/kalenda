package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design tokens mirroring the configurator's tokens but living in the widget module
 * (Glance can't depend on configurator-app). Keep values in sync with
 * `nl.ashhasstudio.kalenda.configurator.ui.theme.Dimensions`.
 */

object WidgetSpacing {
    val cardHorizontalPadding = 17.dp
    val eventPillMarginHorizontal = 18.dp
    val eventPillMarginVertical = 2.dp
    val eventPillPaddingHorizontal = 6.dp
    val eventPillPaddingVertical = 4.dp
    val cardBottomPadding = 16.dp
    val barToTextGap = 10.dp
}

object WidgetShapes {
    val cardRadius = 16.dp
    val pillRadius = 4.dp
    val barRadius = 2.dp
}

object WidgetSizes {
    val eventBarWidth = 3.dp
    val eventBarHeight = 16.dp
    val heroNumberToLabelSpacing = 10.dp
    val heroLabelVerticalOffset = 16.dp
}

object WidgetFontSizes {
    val heroNumber = 34.sp
    val heroLabel = 15.sp
    val body = 14.sp
    val subtle = 13.sp
    val small = 12.sp
}
