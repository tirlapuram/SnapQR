package uk.ac.tees.mad.snapqr

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeNav
import uk.ac.tees.mad.snapqr.ui.homescreen.HomeScreen
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanNav
import uk.ac.tees.mad.snapqr.ui.scanqr.ScanQRScreen
import uk.ac.tees.mad.snapqr.ui.splashscreen.SplashNav
import uk.ac.tees.mad.snapqr.ui.splashscreen.SplashScreen


@Composable
fun SnapQRNav() {
    val navController = rememberNavController()
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
            ScanQRScreen(navController = navController)
        }
    }
}

interface SnapNav {
    val route: String
}