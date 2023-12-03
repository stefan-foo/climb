package rs.elfak.climb.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import rs.elfak.climb.core.Constants
import rs.elfak.climb.ui.Screen.ForgotPasswordScreen
import rs.elfak.climb.ui.Screen.SignInScreen
import rs.elfak.climb.ui.Screen.ProfileScreen
import rs.elfak.climb.ui.Screen.SignUpScreen
import rs.elfak.climb.ui.Screen.MapScreen
import rs.elfak.climb.ui.Screen.VerifyEmailScreen
import rs.elfak.climb.ui.Screen.PersonalizeScreen
import rs.elfak.climb.ui.screens.leaderboard.LeaderboardScreen
import rs.elfak.climb.ui.screens.map.MapScreen
import rs.elfak.climb.ui.screens.map.MapViewModel
import rs.elfak.climb.ui.screens.profile.ProfileScreen
import rs.elfak.climb.ui.screens.sign_in.SignInScreen
import rs.elfak.climb.ui.screens.sign_up.SignUpScreen
import rs.elfak.climb.ui.screens.personalize.PersonalizeScreen
import rs.elfak.climb.ui.screens.track.TrackScreen

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        name = Constants.LEADERBOARD_SCREEN,
        route = Screen.LeaderboardScreen.route,
        icon = Icons.Rounded.Leaderboard,
    ),
    BottomNavItem(
        name = Constants.MAP_SCREEN,
        route = MapScreen.route,
        icon = Icons.Rounded.Home,
    ),
    BottomNavItem(
        name = Constants.PROFILE_SCREEN,
        route = ProfileScreen.route,
        icon = Icons.Rounded.AccountCircle,
    )
)

@Composable
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
fun Navigation(navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        startDestination = MapScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {

        composable(route = Screen.SignInScreen.route) {
            SignInScreen(
                navigateToSignUpScreen = {
                    navController.navigate(SignUpScreen.route)
                },
                navigateToHome = {
                    navController.navigate(Screen.MapScreen.route)
                }
            )
        }
        
        composable(route = Screen.SignUpScreen.route) {
            SignUpScreen(
                navigateBack = { navController.navigate(SignInScreen.route) },
                navigateToPersonalize = { navController.navigate(PersonalizeScreen.route) }
            )
        }

        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(route = Screen.PersonalizeScreen.route) {
            PersonalizeScreen(
                navigateToHome = {
                    navController.navigate(Screen.MapScreen.route)
                }
            )
        }

        composable(route = Screen.MapScreen.route) {
            MapScreen(
                navigateToTrackScreen = { trackId ->
                    navController.navigate("${Screen.TrackScreen.route}/${trackId}") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.LeaderboardScreen.route) {
            LeaderboardScreen()
        }

        composable(
            route = "${Screen.TrackScreen.route}/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.StringType })
        ) {
            TrackScreen(
                trackId = it.arguments?.getString("trackId"),
                navigateBack = {
                    navController.popBackStack(MapScreen.route, false)
                }
            )
        }
    }
}


@Composable
fun AppBottomBar(
    bottomNavItems: List<BottomNavItem>,
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = bottomNavItems.any { it.route == currentDestination?.route }

    if (bottomBarDestination) {
        NavigationBar(
        ) {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                    label = { Text(text = item.name) },
                    selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}