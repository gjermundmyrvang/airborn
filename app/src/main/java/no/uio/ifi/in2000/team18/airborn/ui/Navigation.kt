package no.uio.ifi.in2000.team18.airborn.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team18.airborn.LocalNavController
import no.uio.ifi.in2000.team18.airborn.ui.flightbrief.FlightBriefScreen
import no.uio.ifi.in2000.team18.airborn.ui.home.HomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen()
            }
            composable("flightBrief/{departureIcao}/{arrivalIcao}") {
                FlightBriefScreen()
            }
        }
    }
}
