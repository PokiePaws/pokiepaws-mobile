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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pokiepaws.mobile.data.local.room.entities.NotificationEntity
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
private const val LIST_SPACING = 12
private const val CARD_ELEVATION = 2
private val PRIMARY_TEAL = Color(0xFF7FCEDF)
private val ReminderIconTint = Color(0xFF3B82F6)
private val ReminderIconBg = Color(0xFFDBEAFE)
private val VaccineIconTint = Color(0xFFF97316)
private val VaccineIconBg = Color(0xFFFFEDD5)
private val SuccessIconTint = Color(0xFF22C55E)
private val SuccessIconBg = Color(0xFFDCFCE7)
private val DefaultIconBg = Color(0xFFE3F6FC)

private data class NotifStyle(
    val iconVector: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val borderColor: Color,
)

private fun styleFor(type: String?): NotifStyle =
    when (type?.lowercase()) {
        "reminder" ->
            NotifStyle(
                iconVector = Icons.Default.CalendarToday,
                iconTint = ReminderIconTint,
                iconBg = ReminderIconBg,
                borderColor = ReminderIconTint,
            )
        "vaccine" ->
            NotifStyle(
                iconVector = Icons.Default.MedicalServices,
                iconTint = VaccineIconTint,
                iconBg = VaccineIconBg,
                borderColor = VaccineIconTint,
            )
        "success" ->
            NotifStyle(
                iconVector = Icons.Default.CheckCircle,
                iconTint = SuccessIconTint,
                iconBg = SuccessIconBg,
                borderColor = SuccessIconTint,
            )
        else ->
            NotifStyle(
                iconVector = Icons.Default.Notifications,
                iconTint = PRIMARY_TEAL,
                iconBg = DefaultIconBg,
                borderColor = PRIMARY_TEAL,
            )
    }

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp,
            color = Color.White,
        ) {
            Row(
                modifier =
                    Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                            tint = Color.Black,
                        )
                    }
                    Text(
                        text = "Powiadomienia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                }
                TextButton(onClick = { viewModel.markAllAsRead() }) {
                    Text(
                        text = "Odczytaj wszystkie",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Brak nowych powiadomień 🐾", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(LIST_SPACING.dp),
            ) {
                itemsIndexed(
                    items = notifications,
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
    notification: NotificationEntity,
    animationDelay: Int,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(INITIAL_OFFSET_X) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(notification.id) {
        scope.launch {
            delay(animationDelay.toLong())
            launch {
                alpha.animateTo(1f, tween(durationMillis = ITEM_ANIMATION_DURATION))
            }
            launch {
                offsetX.animateTo(0f, tween(durationMillis = ITEM_ANIMATION_DURATION))
            }
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
    notification: NotificationEntity,
    modifier: Modifier = Modifier,
) {
    val style = styleFor(notification.type)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (!notification.isRead) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .width(INDICATOR_WIDTH.dp)
                            .background(
                                color = style.borderColor,
                                shape =
                                    RoundedCornerShape(
                                        topStart = CARD_ROUNDING.dp,
                                        bottomStart = CARD_ROUNDING.dp,
                                    ),
                            ),
                )
            }

            Row(
                modifier =
                    Modifier
                        .padding(
                            start = if (notification.isRead) 16.dp else 12.dp,
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

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!notification.isRead) Color.Black else Color.Gray,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatTimestamp(notification.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (!notification.isRead) Color.DarkGray else Color.Gray,
                        lineHeight = 18.sp,
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
