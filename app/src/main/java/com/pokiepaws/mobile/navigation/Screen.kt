package com.pokiepaws.mobile.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    data object Register : Screen("register")

    data object EmailVerification : Screen("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }

    data object Home : Screen("home")

    data object Notifications : Screen("notifications")

    data object AnimalList : Screen("animal_list")

    data object AddAnimal : Screen("add_animal")

    data object AnimalDetail : Screen("animal_detail/{animalId}") {
        fun createRoute(animalId: Long) = "animal_detail/$animalId"
    }

    data object AnimalVisitsHistory : Screen("animal_visits_history/{animalId}") {
        fun createRoute(animalId: Long) = "animal_visits_history/$animalId"
    }

    data object ClinicList : Screen("clinic_list")

    data object VetList : Screen("vet_list/{clinicId}") {
        fun createRoute(clinicId: Long) = "vet_list/$clinicId"
    }

    data object AppointmentList : Screen("appointment_list")

    data object CreateVisit : Screen("create_visit/{animalId}") {
        fun createRoute(animalId: Long) = "create_visit/$animalId"
    }

    data object Profile : Screen("profile")

    data object Settings : Screen("settings")

    data object Language : Screen("language")

    data object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: Long) = "appointment_detail/$appointmentId"
    }
}
