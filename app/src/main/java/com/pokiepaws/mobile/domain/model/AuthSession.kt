package com.pokiepaws.mobile.domain.model

data class AuthSession(
    val token: String,
    val role: String,
)
