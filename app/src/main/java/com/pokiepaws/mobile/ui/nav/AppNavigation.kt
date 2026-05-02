package com.pokiepaws.mobile.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.pokiepaws.mobile.data.local.TokenManager
import com.pokiepaws.mobile.ui.animals.*
import com.pokiepaws.mobile.ui.auth.*
import com.pokiepaws.mobile.ui.notifications.NotificationScreen
import com.pokiepaws.mobile.ui.profile.HomeScreen
import com.pokiepaws.mobile.ui.profile.ProfileScreen
import com.pokiepaws.mobile.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    tokenManager: TokenManager,
) {
    val scope = rememberCoroutineScope()
    val tokenState by tokenManager.token.collectAsState(initial = "loading")

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (tokenState == "loading") {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = PokieBlue)
        }
        return
    }

    val dynamicStartDestination = if (tokenState != null) Screen.Home.route else Screen.Login.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar(
                    containerColor = PokieBlueLight,
                    tonalElevation = 10.dp,
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
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    text = item.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            colors =
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor = PokieWhite,
                                    selectedTextColor = PokieWhite,
                                    indicatorColor = PokieWhite.copy(alpha = 0.2f),
                                    unselectedIconColor = PokieWhite,
                                    unselectedTextColor = PokieWhite,
                                ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = dynamicStartDestination,
            // Poprawka: Modifier (duże M) — nowy obiekt, nie reużywamy parametru modifier
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { _, _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Screen.Register.route) },
                    onForgotPasswordClick = { navController.navigate("forgot_password") },
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToVerification = { email ->
                        navController.navigate(Screen.EmailVerification.createRoute(email))
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(
                    onNavigateBack = { navController.popBackStack() },
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
                    onNavigateToAnimals = { navController.navigate(Screen.AnimalList.route) },
                    onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToClinics = { navController.navigate(Screen.ClinicList.route) },
                )
            }

            composable(Screen.Notifications.route) {
                NotificationScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.AnimalList.route) {
                AnimalListScreen(
                    onAddAnimal = { navController.navigate(Screen.AddAnimal.route) },
                    onAnimalClick = { id -> navController.navigate("animal_details/$id") },
                )
            }

            composable(Screen.AddAnimal.route) {
                AddAnimalScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = "animal_details/{animalId}",
                arguments = listOf(navArgument("animalId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getLong("animalId") ?: 0L
                AnimalScreen(
                    animalId = animalId,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Screen.ClinicList.route) { PlaceholderScreen("🏥 Gabinety") }
            composable(Screen.AppointmentList.route) { PlaceholderScreen("📅 Wizyty") }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        scope.launch {
                            tokenManager.clearToken()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
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
