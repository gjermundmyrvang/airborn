package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun GeoSatelliteImage(state: LoadingState<String>, initGeosatellite: () -> Unit) =
    LazyCollapsible(
        header = "Geosatellite image",
        value = state,
        onExpand = initGeosatellite,
        padding = 0.dp
    ) { geoSatellite ->
        ImageComposable(
            uri = geoSatellite,
            contentDescription = "Geosatellite image",
            modifier = Modifier.aspectRatio(1720f / 1280f),
        )
    }