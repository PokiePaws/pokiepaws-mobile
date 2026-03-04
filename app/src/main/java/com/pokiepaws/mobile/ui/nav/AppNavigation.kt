package com.pokiepaws.mobile.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pokiepaws.mobile.ui.auth.LoginScreen

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main
    data object Home : Screen("home")
    data object ClinicList : Screen("clinic_list")

    // Animals
    data object AnimalList : Screen("animal_list")

    // Appointments
    data object AppointmentList : Screen("appointment_list")
    data object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
        fun createRoute(appointmentId: Long) = "appointment_detail/$appointmentId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            // RegisterScreen(...)
        }
        composable(Screen.Home.route) {
            // HomeScreen(...)
        }
        composable(Screen.ClinicList.route) {
            // ClinicListScreen(...)
        }
        composable(Screen.AnimalList.route) {
            // AnimalListScreen(...)
        }
        composable(Screen.AppointmentList.route) {
            // AppointmentListScreen(...)
        }
        composable(Screen.AppointmentDetail.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toLongOrNull()
            // AppointmentDetailScreen(navController, appointmentId)
        }
    }
}