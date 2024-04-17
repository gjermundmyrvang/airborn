package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import no.uio.ifi.in2000.team18.airborn.model.SigmetType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxHomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) =
    Column(modifier = Modifier.fillMaxSize()) {
        val state by homeViewModel.state.collectAsState()
        val airports = state.airports
        val sigmets = state.sigmets
        var selectedAirport by remember { mutableStateOf<Airport?>(null) }
        var isClicked by remember { mutableStateOf(false) }
        var sigmetClicked by rememberSaveable { mutableIntStateOf(0) }
        Box {
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(7.0)
                    center(Point.fromLngLat(11.93, 59.97))
                    pitch(0.0)
                    bearing(0.0)
                }
            }
            MapboxMap(
                Modifier.fillMaxSize(), mapViewportState = mapViewportState
            ) {
                airports.forEach { airport ->
                    Annotation(airport) {
                        selectedAirport = it
                    }
                }
                if (sigmets.isNotEmpty()) {
                    Polygons(sigmets = sigmets) {
                        isClicked = true
                        sigmetClicked = it
                    }
                }
            }
            Column {
                when (val airport = selectedAirport) {
                    null -> {}
                    else -> {
                        homeViewModel.updateSunriseAirport(airport)
                        InfoBox(airport = airport, state) {
                            selectedAirport = null
                        }
                    }
                }
                if (isClicked) {
                    SigmetInfoBox(sigmet = sigmets[sigmetClicked]) {
                        isClicked = false
                    }
                }
            }
        }
    }

@OptIn(MapboxExperimental::class)
@Composable
fun Polygons(
    sigmets: List<Sigmet>, onPolyClicked: (Int) -> Unit
) {
    sigmets.forEachIndexed { index, sigmet ->
        PolygonAnnotation(points = listOf(sigmet.coordinates),
            fillOutlineColorInt = Color.Black.toArgb(),
            fillColorInt = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.4f)
                .toArgb() else Color.Yellow.copy(
                alpha = 0.4f
            ).toArgb(),
            fillOpacity = 0.4,
            onClick = {
                onPolyClicked(index)
                true
            })
    }
}


@OptIn(MapboxExperimental::class)
@Composable
fun Annotation(airport: Airport, onAirportClicked: (Airport) -> Unit) {
    val lon = airport.position.longitude
    val lat = airport.position.latitude
    val selfLocationPoint: Point by remember {
        mutableStateOf(
            Point.fromLngLat(
                lon, lat
            )
        )
    }
    ViewAnnotation(
        options = viewAnnotationOptions {
            geometry(selfLocationPoint)
            allowOverlap(true)
        },
    ) {
        Image(
            painter = painterResource(id = R.drawable.local_airport_24),
            contentDescription = "Marker",
            modifier = Modifier
                .size(20.dp)
                .clickable { onAirportClicked(airport) },
        )
    }
}

@Composable
fun SigmetInfoBox(sigmet: Sigmet, onClose: () -> Unit) = Box(
    modifier = Modifier
        .padding(16.dp)
        .height(300.dp)
        .fillMaxWidth()
        .background(
            color = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.4f) else Color.Yellow.copy(
                alpha = 0.4f
            )
        )
        .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
        .clip(RoundedCornerShape(5.dp))
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Type: ${sigmet.type}")
            IconButton(onClick = { onClose() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close icon")
            }
        }
        Text(text = "Weathermessage: ${sigmet.message}")
    }
}

@Composable
fun InfoBox(airport: Airport, state: HomeViewModel.UiState, onClose: () -> Unit) = Box(
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
        .background(
            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            shape = RoundedCornerShape(5.dp)
        )
        .clip(RoundedCornerShape(5.dp))
) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${airport.name} / ${airport.icao.code}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background
            )
            IconButton(onClick = { onClose() }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close icon",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }

        Column(Modifier.padding(start = 10.dp)) {

            Text(
                "Lat: ${airport.position.latitude}",
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.background
            )
            Text(
                "Lon: ${airport.position.longitude}",
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.background
            )

            Spacer(modifier = Modifier.height(15.dp))

            when (state.sun) {
                is LoadingState.Loading -> Text(
                    text = "Loading sun", color = MaterialTheme.colorScheme.background
                )

                is LoadingState.Error -> Text(
                    text = state.sun.message, color = MaterialTheme.colorScheme.background
                )

                is LoadingState.Success -> state.sun.value?.let { SunComposable(sun = it) }
            }
        }
    }
}


@Composable
fun SunComposable(sun: Sun) {
    Text(
        text = "Sun info:",
        style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.background
    )
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = sun.sunrise,
            modifier = Modifier.height(IntrinsicSize.Min),
            color = MaterialTheme.colorScheme.background
        )

        Image(
            painter = painterResource(id = R.drawable.clearsky_polartwilight),
            contentDescription = "sunrise",
            Modifier
                .rotate(180F)
                .size(35.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = sun.sunset, color = MaterialTheme.colorScheme.background
        )

        Image(
            painter = painterResource(id = R.drawable.clearsky_polartwilight),
            contentDescription = "sunset",
            Modifier.size(35.dp)
        )
        Text(
            text = "(LT)", color = MaterialTheme.colorScheme.background
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewAnnotation() = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painter = painterResource(id = R.drawable.red_marker),
        contentDescription = "Marker",
        modifier = Modifier.size(20.dp)
    )
}
