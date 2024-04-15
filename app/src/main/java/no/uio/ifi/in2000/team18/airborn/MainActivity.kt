package no.uio.ifi.in2000.team18.airborn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team18.airborn.ui.Navigation
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.ConnectivityObserver
import no.uio.ifi.in2000.team18.airborn.ui.connectivity.NetworkConnectivityObserver
import no.uio.ifi.in2000.team18.airborn.ui.theme.AirbornTheme
import javax.inject.Inject

val LocalNavController = compositionLocalOf<NavController> {
    error("CompositionLocal MainNavController not present")
}

@AndroidEntryPoint
class MainActivity @Inject constructor() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val networkStatusFlow = NetworkConnectivityObserver(applicationContext).observe()

        super.onCreate(savedInstanceState)
        setContent {
            val networkStatus by networkStatusFlow.collectAsState(initial = ConnectivityObserver.Status.Available)
            AirbornTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        if (networkStatus != ConnectivityObserver.Status.Available) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Red)
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("$networkStatus")
                            }
                        }
                        Navigation()
                    }
                }
            }
        }
    }
}
