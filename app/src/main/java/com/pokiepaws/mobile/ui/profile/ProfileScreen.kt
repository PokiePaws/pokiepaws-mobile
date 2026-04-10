package com.pokiepaws.mobile.ui.profile

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieCream
import com.pokiepaws.mobile.ui.theme.PokieWhite

data class ProfileMenuItem(
    val icon: ImageVector,
    val label: String,
    val iconColor: Color,
    val bgColor: Color,
)

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val menuItems =
        listOf(
            ProfileMenuItem(
                icon = Icons.Default.Person,
                label = "Dane użytkownika",
                iconColor = Color(0xFF3B82F6),
                bgColor = Color(0xFFDBEAFE),
            ),
            ProfileMenuItem(
                icon = Icons.Default.Language,
                label = "Język (Polski)",
                iconColor = PokieBlue,
                bgColor = PokieCream,
            ),
            ProfileMenuItem(
                icon = Icons.Default.Notifications,
                label = "Ustawienia powiadomień",
                iconColor = Color(0xFFF97316),
                bgColor = Color(0xFFFFEDD5),
            ),
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                label = "Ustawienia aplikacji",
                iconColor = Color(0xFF6B7280),
                bgColor = Color(0xFFE5E7EB),
            ),
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                        )
                        .padding(top = 48.dp, bottom = 56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Mój Profil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokieWhite,
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp),
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
                        Text(text = "👩", fontSize = 36.sp)
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset(y = (-32).dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Admin PokiePaws",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "admin@pokiepaws.pl",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                menuItems.forEachIndexed { index, item ->
                    AnimatedMenuItem(item = item, delayMs = index * 100)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wyloguj się",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
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
                .clickable { },
        shape = RoundedCornerShape(16.dp),
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
                            .clip(RoundedCornerShape(12.dp))
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
                    text = item.label,
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
