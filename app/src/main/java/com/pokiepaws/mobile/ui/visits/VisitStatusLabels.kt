package com.pokiepaws.mobile.ui.visits

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pokiepaws.mobile.R

private const val STATUS_SCHEDULED = "SCHEDULED"
private const val STATUS_CONFIRMED = "CONFIRMED"
private const val STATUS_CANCELLED = "CANCELLED"
private const val STATUS_COMPLETED = "COMPLETED"

fun String.isScheduledVisitStatus(): Boolean = equals(STATUS_SCHEDULED, ignoreCase = true)

fun String.isUpcomingVisitStatus(): Boolean =
    equals(STATUS_SCHEDULED, ignoreCase = true) ||
        equals(STATUS_CONFIRMED, ignoreCase = true)

fun String.isCancelledVisitStatus(): Boolean = equals(STATUS_CANCELLED, ignoreCase = true)

fun visitStatusSearchText(status: String): String =
    when (status.uppercase()) {
        STATUS_SCHEDULED -> "$status scheduled zaplanowana"
        STATUS_CONFIRMED -> "$status confirmed potwierdzona"
        STATUS_CANCELLED -> "$status cancelled anulowana"
        STATUS_COMPLETED -> "$status completed zakonczona"
        else -> status
    }.lowercase()

@Composable
fun localizedVisitStatusLabel(status: String): String =
    status.visitStatusLabelRes()?.let { stringResource(it) }
        ?: status.toFallbackVisitStatusLabel()

@StringRes
private fun String.visitStatusLabelRes(): Int? =
    when (uppercase()) {
        STATUS_SCHEDULED -> R.string.visit_status_scheduled
        STATUS_CONFIRMED -> R.string.visit_status_confirmed
        STATUS_CANCELLED -> R.string.visit_status_cancelled
        STATUS_COMPLETED -> R.string.visit_status_completed
        else -> null
    }

private fun String.toFallbackVisitStatusLabel(): String =
    replace('_', ' ')
        .lowercase()
        .replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
