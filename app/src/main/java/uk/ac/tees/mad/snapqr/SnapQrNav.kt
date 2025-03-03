package uk.ac.tees.mad.snapqr

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.ac.tees.mad.snapqr.ui.auth.LoginNav
import uk.ac.tees.mad.snapqr.ui.auth.LoginScreen
import uk.ac.tees.mad.snapqr.ui.auth.RegisterNav
import uk.ac.tees.mad.snapqr.ui.auth.RegisterScreen
import uk.ac.tees.mad.snapqr.ui.favoritescan.FavoriteNav
import uk.ac.tees.mad.snapqr.ui.favoritescan.FavoriteScansScreen
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeNav
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeScreen
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailNav
import uk.ac.tees.mad.snapqr.ui.scandetails.ScanDetailsScreen
import uk.ac.tees.mad.snapqr.ui.scanhistory.ScanHistoryNav
import uk.ac.tees.mad.snapqr.ui.scanhistory.ScanHistoryScreen
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRScreen
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRViewModel
import uk.ac.tees.mad.snapqr.ui.settingscreen.SettingsNav
import uk.ac.tees.mad.snapqr.ui.settingscreen.SettingsScreen
import uk.ac.tees.mad.snapqr.ui.splashscreen.SplashNav
import uk.ac.tees.mad.snapqr.ui.splashscreen.SplashScreen


@Composable
fun SnapQRNav(scanType: String?, message: String?) {
    val navController = rememberNavController()
    val scanQRViewModel: ScanQRViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = SplashNav.route) {
        composable(route = SplashNav.route) {
            SplashScreen(navigateBack = {
                navController.navigate(HomeNav.route)
            })
        }
        composable(route = HomeNav.route) {
            HomeScreen(navController)
        }
        composable(route = ScanNav.route) {
            ScanQRScreen(navController = navController, scanQRViewModel = scanQRViewModel)
        }

        composable(ScanDetailNav.route) {
            ScanDetailsScreen(navController = navController, scanQRViewModel = scanQRViewModel)
        }

        composable(ScanHistoryNav.route) {
            ScanHistoryScreen(navController = navController, scanQRViewModel = scanQRViewModel)
        }
        composable(
            route = LoginNav.route,
            arguments = listOf(navArgument("fromFavoriteScreen") { defaultValue = "false" })
        ) { backStackEntry ->
            val fromFavoriteScreen =
                backStackEntry.arguments?.getString("fromFavoriteScreen") == "true"
            LoginScreen(navController, fromFavoriteScreen)
        }
        composable(
            route = RegisterNav.route,
            arguments = listOf(navArgument("fromFavoriteScreen") { defaultValue = "false" })
        ) { backStackEntry ->
            val fromFavoriteScreen =
                backStackEntry.arguments?.getString("fromFavoriteScreen") == "true"
            RegisterScreen(navController, fromFavoriteScreen)
        }

        composable(FavoriteNav.route) {
            FavoriteScansScreen(navController = navController)
        }

        composable(SettingsNav.route) {
            SettingsScreen(navController = navController, scanQRViewModel = scanQRViewModel)
        }
    }
    if (scanType != null && message != null) {
        scanQRViewModel.updateState(scanType, message, false)
        navController.navigate(ScanDetailNav.route)
    }
}

interface SnapNav {
    val route: String
}