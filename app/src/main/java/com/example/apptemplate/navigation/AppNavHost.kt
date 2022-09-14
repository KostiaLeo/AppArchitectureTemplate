package com.example.apptemplate.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.apptemplate.ui.main.MainScreen
import com.example.apptemplate.ui.secondary.SecondaryScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main/{param}"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable(
            route = "main/{param}",
            arguments = listOf(navArgument("param") { type = NavType.StringType; defaultValue = "Lesgo" })
        ) { backStackEntry ->
            val param = backStackEntry.arguments?.getString("param")
            MainScreen(
                param = param,
                onNavigateToScreen2 = { navController.navigate("secondary/Hello!") }
            )
        }
        composable(
            route = "secondary/{param}",
            arguments = listOf(navArgument("param") { type = NavType.StringType })
        ) { backStackEntry ->
            val param = backStackEntry.arguments?.getString("param")
            SecondaryScreen(
                param = param,
                onNavigateToScreen1 = { navController.navigate("main/Go") }
            )
        }
    }
}