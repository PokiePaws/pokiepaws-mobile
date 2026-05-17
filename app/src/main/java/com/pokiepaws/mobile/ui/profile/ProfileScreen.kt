package com.pokiepaws.mobile.ui.profile

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieCream
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 48
private const val AVATAR_OFFSET = -48
private const val CONTENT_OFFSET = -32
private const val MENU_ANIMATION_DELAY = 100
private const val CARD_ROUNDING = 24
private const val ICON_BG_ROUNDING = 12
private const val LOGOUT_COLOR = 0xFFEF4444

private val PersonIconTint = Color(0xFF3B82F6)
private val PersonIconBg = Color(0xFFDBEAFE)
private val NotificationIconTint = Color(0xFFF97316)
private val NotificationIconBg = Color(0xFFFFEDD5)
private val SettingsIconTint = Color(0xFF6B7280)
private val SettingsIconBg = Color(0xFFE5E7EB)

data class ProfileMenuItem(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val iconColor: Color,
    val bgColor: Color,
    val onClick: () -> Unit = {},
)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuItems =
        remember(onLanguageClick, onSettingsClick) {
            listOf(
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    labelRes = R.string.profile_user_data,
                    iconColor = PersonIconTint,
                    bgColor = PersonIconBg,
                ),
                ProfileMenuItem(
                    icon = Icons.Default.Language,
                    labelRes = R.string.profile_language,
                    iconColor = PokieBlue,
                    bgColor = PokieCream,
                    onClick = onLanguageClick,
                ),
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    labelRes = R.string.profile_notification_settings,
                    iconColor = NotificationIconTint,
                    bgColor = NotificationIconBg,
                ),
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    labelRes = R.string.profile_app_settings,
                    iconColor = SettingsIconTint,
                    bgColor = SettingsIconBg,
                    onClick = onSettingsClick,
                ),
            )
        }

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
                    .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset(y = AVATAR_OFFSET.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(PokieWhite),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(PokieCream),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "A", fontSize = 36.sp)
                }
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(y = CONTENT_OFFSET.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.profile_user_name),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PokieBlueDark,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.profile_user_email),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            menuItems.forEachIndexed { index, item ->
                AnimatedMenuItem(item = item, delayMs = index * MENU_ANIMATION_DELAY)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            LogoutButton(onLogout)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogout),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                            .background(PokieWhite),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = Color(LOGOUT_COLOR),
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.profile_logout),
                    fontWeight = FontWeight.Bold,
                    color = Color(LOGOUT_COLOR),
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(LOGOUT_COLOR).copy(alpha = 0.5f),
            )
        }
    }
}

@Composable
private fun AnimatedMenuItem(
    item: ProfileMenuItem,
    delayMs: Int,
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = delayMs),
        label = "menuItemAlpha",
    )

    LaunchedEffect(Unit) { visible = true }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .alpha(alpha)
                .clickable(onClick = item.onClick),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors = CardDefaults.cardColors(containerColor = PokieWhite),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                            .background(item.bgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconColor,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(item.labelRes),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
