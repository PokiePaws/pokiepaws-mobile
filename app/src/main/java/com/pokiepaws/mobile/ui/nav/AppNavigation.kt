package com.pokiepaws.mobile.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pokiepaws.mobile.ui.auth.EmailVerificationScreen
import com.pokiepaws.mobile.ui.auth.ForgotPasswordScreen
import com.pokiepaws.mobile.ui.auth.LoginScreen
import com.pokiepaws.mobile.ui.auth.RegisterScreen
import com.pokiepaws.mobile.ui.profile.HomeScreen
import com.pokiepaws.mobile.ui.profile.ProfileScreen
import com.pokiepaws.mobile.ui.theme.PokieBlue
import com.pokiepaws.mobile.ui.theme.PokieBlueLight
import com.pokiepaws.mobile.ui.theme.PokieLightText
import com.pokiepaws.mobile.ui.theme.PokieWhite

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar(
                    containerColor = PokieWhite,
                    tonalElevation = 8.dp,
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            colors =
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor = PokieBlue,
                                    selectedTextColor = PokieBlue,
                                    indicatorColor = PokieBlueLight.copy(alpha = 0.2f),
                                    unselectedIconColor = PokieLightText,
                                    unselectedTextColor = PokieLightText,
                                ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { token, role ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    onForgotPasswordClick = {
                        navController.navigate("forgot_password")
                    },
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToVerification = { email ->
                        navController.navigate(Screen.EmailVerification.createRoute(email))
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onEmailSent = { email ->
                        navController.navigate(Screen.EmailVerification.createRoute(email))
                    },
                )
            }

            composable(
                route = Screen.EmailVerification.route,
                arguments = listOf(navArgument("email") { type = NavType.StringType }),
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                EmailVerificationScreen(
                    email = email,
                    onBackToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToAnimals = { navController.navigate(Screen.AnimalList.route) },
                    onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                    onNavigateToClinics = { navController.navigate(Screen.ClinicList.route) },
                )
            }

            composable(Screen.ClinicList.route) {
                PlaceholderScreen("🏥")
            }

            composable(Screen.AnimalList.route) {
                PlaceholderScreen("🐾")
            }

            composable(Screen.AppointmentList.route) {
                PlaceholderScreen("📅")
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.AppointmentDetail.route) { backStackEntry ->
                // Logika dla detali wizyty
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavItems =
    listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
        BottomNavItem(Screen.AnimalList, "Zwierzęta", Icons.Default.Pets),
        BottomNavItem(Screen.AppointmentList, "Wizyty", Icons.Default.CalendarMonth),
        BottomNavItem(Screen.ClinicList, "Gabinety", Icons.Default.LocalHospital),
        BottomNavItem(Screen.Profile, "Profil", Icons.Default.Person),
    )

private val bottomNavRoutes = bottomNavItems.map { it.screen.route }.toSet()
