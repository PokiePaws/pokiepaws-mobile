package com.pokiepaws.mobile.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pokiepaws.mobile.ui.auth.EmailVerificationScreen
import com.pokiepaws.mobile.ui.auth.LoginScreen
import com.pokiepaws.mobile.ui.auth.RegisterScreen
import com.pokiepaws.mobile.ui.profile.HomeScreen

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
    )
    {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { token, role ->
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToVerification = { email ->
                    navController.navigate(Screen.EmailVerification.createRoute(email))
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EmailVerification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                onBackToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ClinicList.route) { /* ClinicListScreen(...) */ }
        composable(Screen.AnimalList.route) { /* AnimalListScreen(...) */ }
        composable(Screen.AppointmentList.route) { /* AppointmentListScreen(...) */ }
        composable(Screen.AppointmentDetail.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toLongOrNull()
            // AppointmentDetailScreen(navController, appointmentId)
        }
    }
}