package no.uio.ifi.in2000.team18.airborn.ui.flightbrief

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@Composable
fun GeoSatelliteImage(state: LoadingState<String>, initGeosatellite: () -> Unit) =
    LazyCollapsible(
        header = "Geosatellite image",
        value = state,
        onExpand = initGeosatellite
    ) { geoSatellite ->
        ImageComposable(
            uri = geoSatellite,
            contentDescription = "",
            modifier = Modifier.aspectRatio(1280f / 1720f),
        )
    }