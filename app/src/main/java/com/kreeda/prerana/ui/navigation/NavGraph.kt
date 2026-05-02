package com.kreeda.prerana.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kreeda.prerana.ui.screen.athletes.AddEditAthleteScreen
import com.kreeda.prerana.ui.screen.athletes.AthleteProfileScreen
import com.kreeda.prerana.ui.screen.athletes.AthletesListScreen
import com.kreeda.prerana.ui.screen.batch.BatchEntryScreen
import com.kreeda.prerana.ui.screen.home.HomeScreen
import com.kreeda.prerana.ui.screen.leaderboard.LeaderboardScreen
import com.kreeda.prerana.ui.screen.performance.PerformanceHistoryScreen
import com.kreeda.prerana.ui.screen.performance.TalentCurveScreen
import com.kreeda.prerana.ui.screen.settings.SettingsScreen
import com.kreeda.prerana.ui.screen.timer.ChronoScreen

/**
 * Main navigation graph for Kreeda-Prerana Scout.
 * Defines all screens and their transitions.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        // ── Home Dashboard ──
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // ── Athletes List ──
        composable(Screen.AthletesList.route) {
            AthletesListScreen(navController = navController)
        }

        // ── Athlete Profile ──
        composable(
            route = Screen.AthleteProfile.route,
            arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: return@composable
            AthleteProfileScreen(
                athleteId = athleteId,
                navController = navController
            )
        }

        // ── Add/Edit Athlete ──
        composable(
            route = Screen.AddEditAthlete.route,
            arguments = listOf(
                navArgument("athleteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: -1L
            AddEditAthleteScreen(
                athleteId = if (athleteId == -1L) null else athleteId,
                navController = navController
            )
        }

        // ── Chronometer ──
        composable(Screen.Chronometer.route) {
            ChronoScreen(navController = navController)
        }

        // ── Performance History ──
        composable(
            route = Screen.PerformanceHistory.route,
            arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: return@composable
            PerformanceHistoryScreen(
                athleteId = athleteId,
                navController = navController
            )
        }

        // ── Talent Curve ──
        composable(
            route = Screen.TalentCurve.route,
            arguments = listOf(
                navArgument("athleteId") { type = NavType.LongType },
                navArgument("eventTypeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: return@composable
            val eventTypeId = backStackEntry.arguments?.getLong("eventTypeId") ?: return@composable
            TalentCurveScreen(
                athleteId = athleteId,
                eventTypeId = eventTypeId,
                navController = navController
            )
        }

        // ── Leaderboard ──
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController = navController)
        }

        // ── Batch Entry ──
        composable(Screen.BatchEntry.route) {
            BatchEntryScreen(navController = navController)
        }

        // ── Settings ──
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
