package org.example.project.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.presentation.auth.AuthScreen
import org.example.project.presentation.auth.AuthViewModel
import org.example.project.presentation.main.MainScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    startDestination: Screen = Screen.Auth
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Auth> {
            val viewModel: AuthViewModel = koinViewModel()
            AuthScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(Screen.Main) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.Main> {
            MainScreen()
        }
    }
}
