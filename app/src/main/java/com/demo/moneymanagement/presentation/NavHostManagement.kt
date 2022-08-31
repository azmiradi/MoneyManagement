package com.demo.moneymanagement.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.moneymanagement.presentation.screens.auth.login.LoginScreen
import com.demo.moneymanagement.presentation.screens.auth.signup.SignupScreen
import com.demo.moneymanagement.screens.SplashScreen

@Composable
fun NavHostManagement() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestination.Splash.destination
    ) {
        composable(NavigationDestination.Splash.destination) {
            SplashScreen() {
                navController.navigate(it.destination) {
                    popUpTo(NavigationDestination.Splash.destination) {
                        inclusive = true
                    }
                }
            }
        }
        composable(NavigationDestination.Login.destination) {
            LoginScreen(onNavigate = {
                navController.navigate(it.destination)
            })
        }
        composable(NavigationDestination.Signup.destination) {
            SignupScreen(onNavigate = {
                navController.navigate(it.destination)
            })
        }

    }
}