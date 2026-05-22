package com.pokiepaws.mobile.data.remote.dto.visit

import kotlinx.serialization.Serializable

@Serializable
data class AvailableSlotsResponse(
    val clinicId: Long,
    val vetUserId: Long,
    val date: String,
    val slotMinutes: Int,
    val workdayStart: String,
    val workdayEnd: String,
    val availableStarts: List<String>,
)
