package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object FinSmartDimens {
    val ScreenPadding = 16.dp
    val CardPadding = 16.dp
    val SectionGap = 16.dp
    val ItemGap = 12.dp
    val CardRadius = 20.dp
    val PillRadius = 50.dp
    val MinTouchTarget = 48.dp
}

@Composable
fun FinSmartCard(
    modifier: Modifier = Modifier,
    containerColor: Color = LocalFinSmartColors.current.surface,
    accentBorderColor: Color? = null,
    cornerRadius: Dp = FinSmartDimens.CardRadius,
    contentPadding: PaddingValues = PaddingValues(FinSmartDimens.CardPadding),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalFinSmartColors.current
    val resolvedBorder = when {
        accentBorderColor != null -> accentBorderColor.copy(alpha = if (colors.isDarkNeon) 0.55f else 0.35f)
        colors.isDarkNeon -> colors.cardBorder
        else -> colors.cardBorder.copy(alpha = 0.7f)
    }

    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = modifier
        .then(
            if (colors.isDarkNeon && accentBorderColor != null) {
                Modifier.shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = accentBorderColor.copy(alpha = 0.25f),
                    spotColor = accentBorderColor.copy(alpha = 0.30f)
                )
            } else if (!colors.isDarkNeon) {
                Modifier.shadow(
                    elevation = 2.dp,
                    shape = shape,
                    ambientColor = Color(0x14087F5B),
                    spotColor = Color(0x14087F5B)
                )
            } else {
                Modifier
            }
        )
        .clip(shape)
        .background(containerColor)
        .border(BorderStroke(1.dp, resolvedBorder), shape)
        .then(
            if (onClick != null) {
                Modifier
                    .defaultMinSize(minHeight = FinSmartDimens.MinTouchTarget)
                    .clickable(onClick = onClick)
            } else {
                Modifier
            }
        )

    Column(
        modifier = baseModifier.padding(contentPadding),
        content = content
    )
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    startColor: Color = LocalFinSmartColors.current.primary,
    endColor: Color = LocalFinSmartColors.current.secondary,
    contentColor: Color = if (LocalFinSmartColors.current.isDarkNeon) Color(0xFF04141D) else Color.White
) {
    val shape = RoundedCornerShape(16.dp)
    val brush = Brush.horizontalGradient(
        colors = if (enabled) listOf(startColor, endColor)
        else listOf(startColor.copy(alpha = 0.45f), endColor.copy(alpha = 0.45f))
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .clip(shape)
            .background(brush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val colors = LocalFinSmartColors.current
    val bgAlpha = if (colors.isDarkNeon) 0.18f else 0.14f
    val borderAlpha = if (colors.isDarkNeon) 0.55f else 0.35f
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(FinSmartDimens.PillRadius),
        color = color.copy(alpha = bgAlpha),
        border = BorderStroke(1.dp, color.copy(alpha = borderAlpha))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun OfflineStatusChip(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalFinSmartColors.current
    val targetColor by animateColorAsState(
        targetValue = if (isOnline) colors.success else colors.warning,
        label = "online_status_color"
    )
    val label = if (isOnline) "Online" else "Offline Mode"
    val icon = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff
    StatusPill(
        text = label,
        color = targetColor,
        icon = icon,
        modifier = modifier.semantics {
            contentDescription = if (isOnline) "Device is online" else "Device is offline, showing cached lessons and local tools"
        }
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalFinSmartColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.textPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = colors.secondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun IconBadge(
    icon: ImageVector,
    tint: Color,
    backgroundColor: Color,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
