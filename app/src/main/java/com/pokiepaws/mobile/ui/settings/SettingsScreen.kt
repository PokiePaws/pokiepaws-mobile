package com.pokiepaws.mobile.ui.settings

import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.pokiepaws.mobile.R
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueDark
import com.pokiepaws.mobile.ui.theme.PokieCream
import com.pokiepaws.mobile.ui.theme.PokieWhite

private const val ENGLISH_TAG = "en"
private const val POLISH_TAG = "pl"

private const val HEADER_ROUNDING = 32
private const val HEADER_TOP_PADDING = 48
private const val HEADER_BOTTOM_PADDING = 48
private const val CONTENT_OFFSET = -16
private const val CARD_ROUNDING = 24
private const val ICON_BG_ROUNDING = 12
private const val MENU_ANIMATION_DELAY = 80

data class LanguageOption(
    val tag: String,
    val labelRes: Int,
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedLanguageTag by remember {
        mutableStateOf(
            AppCompatDelegate.getApplicationLocales()
                .get(0)
                ?.language
                ?: ""
        )
    }

    val languageOptions =
        remember {
            listOf(
                LanguageOption(tag = "", labelRes = R.string.settings_language_system),
                LanguageOption(tag = POLISH_TAG, labelRes = R.string.settings_language_polish),
                LanguageOption(tag = ENGLISH_TAG, labelRes = R.string.settings_language_english),
            )
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        // ── Header ──────────────────────────────────────────────────────────
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
                    contentDescription = stringResource(R.string.settings_back),
                    tint = PokieWhite,
                )
            }
            Text(
                text = stringResource(R.string.settings_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PokieWhite,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // ── Content ─────────────────────────────────────────────────────────
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(y = CONTENT_OFFSET.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
        ) {
            // Section header card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(ICON_BG_ROUNDING.dp))
                                .background(PokieCream),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = PokieBlue,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.settings_language_title),
                            fontWeight = FontWeight.Bold,
                            color = PokieBlueDark,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.settings_language_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Language option cards
            languageOptions.forEachIndexed { index, option ->
                AnimatedLanguageCard(
                    labelRes = option.labelRes,
                    selected = selectedLanguageTag == option.tag,
                    delayMs = index * MENU_ANIMATION_DELAY,
                    onClick = {
                        selectedLanguageTag = option.tag
                        AppCompatDelegate.setApplicationLocales(
                            if (option.tag.isBlank()) {
                                LocaleListCompat.getEmptyLocaleList()
                            } else {
                                LocaleListCompat.forLanguageTags(option.tag)
                            },
                        )
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnimatedLanguageCard(
    labelRes: Int,
    selected: Boolean,
    delayMs: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, delayMillis = delayMs),
        label = "languageCardAlpha",
    )

    LaunchedEffect(Unit) { visible = true }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(alpha)
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(CARD_ROUNDING.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = if (selected) PokieCream else PokieWhite,
            ),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(labelRes),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) PokieBlueDark else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors =
                    RadioButtonDefaults.colors(
                        selectedColor = PokieBlue,
                    ),
            )
        }
    }
}
