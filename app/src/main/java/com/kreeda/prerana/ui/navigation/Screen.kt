package com.kreeda.prerana.ui.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AthletesList : Screen("athletes_list")
    object AthleteProfile : Screen("athlete_profile/{athleteId}") {
        fun createRoute(athleteId: Long) = "athlete_profile/$athleteId"
    }
    object AddEditAthlete : Screen("add_edit_athlete?athleteId={athleteId}") {
        fun createRoute(athleteId: Long? = null) =
            if (athleteId != null) "add_edit_athlete?athleteId=$athleteId"
            else "add_edit_athlete"
    }
    object Chronometer : Screen("chronometer")
    object DistanceLogger : Screen("distance_logger")
    object PerformanceHistory : Screen("performance_history/{athleteId}") {
        fun createRoute(athleteId: Long) = "performance_history/$athleteId"
    }
    object TalentCurve : Screen("talent_curve/{athleteId}/{eventTypeId}") {
        fun createRoute(athleteId: Long, eventTypeId: Long) =
            "talent_curve/$athleteId/$eventTypeId"
    }
    object Leaderboard : Screen("leaderboard")
    object BatchEntry : Screen("batch_entry")
    object Settings : Screen("settings")
}
