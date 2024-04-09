package no.uio.ifi.in2000.team18.airborn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team18.airborn.ui.Navigation
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme

val LocalNavController = compositionLocalOf<NavController> {
    error("CompositionLocal MainNavController not present")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirbornTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}
