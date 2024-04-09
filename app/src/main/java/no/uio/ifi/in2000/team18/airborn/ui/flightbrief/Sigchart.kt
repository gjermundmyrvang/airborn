package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import no.uio.ifi.in2000.team18.airborn.model.Sigchart
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun Sigchart(state: LoadingState<List<Sigchart>>) =
    LoadingCollapsible(state, header = "Sigchart") { sigcharts ->
        val sigchart = sigcharts.last()
        val zoomState = rememberZoomState()
        SubcomposeAsyncImage(modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { clip = true }
            .zoomable(zoomState),
            contentScale = ContentScale.FillWidth,
            model = ImageRequest.Builder(LocalContext.current)
                .data(sigchart.uri)
                .setHeader("User-Agent", "Team18")
                .crossfade(500)
                .build(),
            loading = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 1.dp
                    )
                }
            },
            contentDescription = "Image of sigchart. Updated at ${sigchart.updated}"
        )
    }
