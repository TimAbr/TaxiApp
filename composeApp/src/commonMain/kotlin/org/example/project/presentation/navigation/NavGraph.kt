package org.example.project.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.presentation.auth.AuthScreen
import org.example.project.presentation.auth.AuthViewModel
import org.example.project.presentation.main.MainEvent
import org.example.project.presentation.main.MainScreen
import org.example.project.presentation.main.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    startDestination: Screen = Screen.Auth,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screen.Auth> {
            val viewModel: AuthViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()

            AuthScreen(
                state = state,
                onLoginClick = { viewModel.loginWithGoogle() },
                onClearError = { viewModel.clearError() },
                onNavigateToMain = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable<Screen.Main> {
            val viewModel: MainViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()

            MainScreen(
                state = state,
                events = viewModel.events,
                onLogoutSuccess = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Main) { inclusive = true }
                    }
                },
                onLogoutRequest = { viewModel.showLogoutConfirmation() },
                onLogoutConfirm = { viewModel.logout() },
                onLogoutDismiss = { viewModel.hideLogoutConfirmation() },
            )
        }
    }
}
