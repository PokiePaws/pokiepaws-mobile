package com.pokiepaws.mobile.ui.nav

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    data object Register : Screen("register")

    data object EmailVerification : Screen("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }

    data object Home : Screen("home")

    data object ClinicList : Screen("clinic_list")

    data object AnimalList : Screen("animal_list")

    data object AppointmentList : Screen("appointment_list")

    data object Profile : Screen("profile")

    data object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: Long) = "appointment_detail/$appointmentId"
    }
}
