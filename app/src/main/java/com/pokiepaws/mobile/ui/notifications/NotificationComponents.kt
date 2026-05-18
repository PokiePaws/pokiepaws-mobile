package com.pokiepaws.mobile.ui.notifications

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.domain.model.AppNotification
import com.pokiepaws.mobile.util.theme.PokieBlueDark
import com.pokiepaws.mobile.util.theme.PokieWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ANIMATION_DELAY_PER_ITEM_MS = 80
private const val ITEM_ANIMATION_DURATION = 300
private const val INITIAL_OFFSET_X = -20f
private const val CARD_ROUNDING = 16
private const val ICON_BG_SIZE = 48
private const val ICON_SIZE = 24
private const val INDICATOR_WIDTH = 4
private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 48

private val ReminderIconTint = Color(0xFF3B82F6)
private val ReminderIconBg = Color(0xFFDBEAFE)
private val VaccineIconTint = Color(0xFFF97316)
private val VaccineIconBg = Color(0xFFFFEDD5)
private val SuccessIconTint = Color(0xFF22C55E)
private val SuccessIconBg = Color(0xFFDCFCE7)
private val DefaultIconBg = Color(0xFFE3F6FC)
private val DefaultIconTint = Color(0xFF7FCEDF)

private data class NotifStyle(
    val iconVector: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val borderColor: Color,
)

private fun styleFor(type: String?): NotifStyle =
    when (type?.uppercase()) {
        "VISIT_REMINDER_24H",
        "VISIT_REMINDER_1H",
        "REMINDER",
        ->
            NotifStyle(
                iconVector = Icons.Default.CalendarToday,
                iconTint = ReminderIconTint,
                iconBg = ReminderIconBg,
                borderColor = ReminderIconTint,
            )
        "VACCINATION_REMINDER",
        "VACCINE",
        ->
            NotifStyle(
                iconVector = Icons.Default.MedicalServices,
                iconTint = VaccineIconTint,
                iconBg = VaccineIconBg,
                borderColor = VaccineIconTint,
            )
        "VISIT_CONFIRMED",
        "PRESCRIPTION_CREATED",
        "VISIT_MEDICAL_DATA_UPDATED",
        "SUCCESS",
        ->
            NotifStyle(
                iconVector = Icons.Default.CheckCircle,
                iconTint = SuccessIconTint,
                iconBg = SuccessIconBg,
                borderColor = SuccessIconTint,
            )
        else ->
            NotifStyle(
                iconVector = Icons.Default.Notifications,
                iconTint = DefaultIconTint,
                iconBg = DefaultIconBg,
                borderColor = DefaultIconTint,
            )
    }

@Composable
fun NotificationContent(
    notifications: NotificationItems,
    onBack: () -> Unit,
    onMarkAllAsRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape =
                            RoundedCornerShape(
                                bottomStart = HEADER_ROUNDING.dp,
                                bottomEnd = HEADER_ROUNDING.dp,
                            ),
                    )
                    .padding(top = HEADER_TOP_PADDING.dp, bottom = HEADER_BOTTOM_PADDING.dp)
                    .padding(horizontal = 8.dp),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_content_description),
                    tint = PokieWhite,
                )
            }
            Text(
                text = stringResource(R.string.notifications_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
                modifier = Modifier.align(Alignment.Center),
            )
            TextButton(
                onClick = onMarkAllAsRead,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    text = stringResource(R.string.notifications_mark_all_read),
                    color = PokieWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }

        if (notifications.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier =
                            Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(DefaultIconBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = DefaultIconTint,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.notifications_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(
                    items = notifications.items,
                    key = { _, n -> n.id },
                ) { index, notification ->
                    AnimatedNotificationItem(
                        notification = notification,
                        animationDelay = index * ANIMATION_DELAY_PER_ITEM_MS,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedNotificationItem(
    notification: AppNotification,
    animationDelay: Int,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(INITIAL_OFFSET_X) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(notification.id) {
        scope.launch {
            delay(animationDelay.toLong())
            launch { alpha.animateTo(1f, tween(durationMillis = ITEM_ANIMATION_DURATION)) }
            launch { offsetX.animateTo(0f, tween(durationMillis = ITEM_ANIMATION_DURATION)) }
        }
    }

    NotificationItem(
        notification = notification,
        modifier =
            modifier.graphicsLayer {
                this.alpha = alpha.value
                this.translationX = offsetX.value.dp.toPx()
            },
    )
}

@Composable
fun NotificationItem(
    notification: AppNotification,
    modifier: Modifier = Modifier,
) {
    val style = styleFor(notification.type)
    val isUnread = !notification.isRead

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            UnreadIndicator(isUnread = isUnread, borderColor = style.borderColor)
            NotificationBody(notification = notification, style = style, isUnread = isUnread)
        }
    }
}

@Composable
private fun UnreadIndicator(
    isUnread: Boolean,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
    if (!isUnread) return
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .width(INDICATOR_WIDTH.dp)
                .background(
                    color = borderColor,
                    shape =
                        RoundedCornerShape(
                            topStart = CARD_ROUNDING.dp,
                            bottomStart = CARD_ROUNDING.dp,
                        ),
                ),
    )
}

@Composable
private fun NotificationBody(
    notification: AppNotification,
    style: NotifStyle,
    isUnread: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.padding(
                start = if (isUnread) 12.dp else 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp,
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(ICON_BG_SIZE.dp)
                    .clip(CircleShape)
                    .background(style.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = style.iconVector,
                contentDescription = null,
                tint = style.iconTint,
                modifier = Modifier.size(ICON_SIZE.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnread) PokieBlueDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTimestamp(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.content,
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
