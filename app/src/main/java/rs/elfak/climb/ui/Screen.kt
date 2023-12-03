package rs.elfak.climb.ui

import rs.elfak.climb.core.Constants.SIGN_IN_SCREEN
import rs.elfak.climb.core.Constants.FORGOT_PASSWORD_SCREEN
import rs.elfak.climb.core.Constants.MAP_SCREEN
import rs.elfak.climb.core.Constants.PERSONALIZE_SCREEN
import rs.elfak.climb.core.Constants.SIGN_UP_SCREEN
import rs.elfak.climb.core.Constants.VERIFY_EMAIL_SCREEN
import rs.elfak.climb.core.Constants.PROFILE_SCREEN
import rs.elfak.climb.core.Constants.LEADERBOARD_SCREEN
import rs.elfak.climb.core.Constants.TRACK_SCREEN

sealed class Screen(val route: String) {
    object SignInScreen: Screen(SIGN_IN_SCREEN)
    object ForgotPasswordScreen: Screen(FORGOT_PASSWORD_SCREEN)
    object SignUpScreen: Screen(SIGN_UP_SCREEN)
    object VerifyEmailScreen: Screen(VERIFY_EMAIL_SCREEN)
    object ProfileScreen: Screen(PROFILE_SCREEN)
    object PersonalizeScreen: Screen(PERSONALIZE_SCREEN)
    object MapScreen: Screen(MAP_SCREEN)
    object LeaderboardScreen: Screen(LEADERBOARD_SCREEN)
    object TrackScreen: Screen(TRACK_SCREEN)
}