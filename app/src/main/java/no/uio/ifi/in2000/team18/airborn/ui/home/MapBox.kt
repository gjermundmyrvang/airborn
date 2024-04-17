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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Sun
import no.uio.ifi.in2000.team18.airborn.ui.common.LoadingState

@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxHomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) =
    Column(modifier = Modifier.fillMaxSize()) {
        val state by homeViewModel.state.collectAsState()
        val airports = state.airports
        var selectedAirport by remember { mutableStateOf<Airport?>(null) }
        val osloPolygon = listOf(
            listOf(
                Point.fromLngLat(10.580, 59.890),
                Point.fromLngLat(11.360, 59.890),
                Point.fromLngLat(11.360, 60.150),
                Point.fromLngLat(10.580, 60.150),
                Point.fromLngLat(10.580, 59.890)
            )
        )
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
                // TODO give Polygon composable real sigmet/airmet coordinates
                Polygon(points = osloPolygon)
            }
            when (val airport = selectedAirport) {
                null -> {}
                else -> {
                    homeViewModel.updateSunriseAirport(airport)
                    InfoBox(airport = airport, state) {
                        selectedAirport = null
                    }
                }
            }
        }
    }

@OptIn(MapboxExperimental::class)
@Composable
fun Polygon(points: List<List<Point>>) { // TODO implement onclick functionality and display sigmet/airmet information
    PolygonAnnotation(
        points = points,
        fillOutlineColorInt = Color.Black.toArgb(),
        fillColorInt = Color.Cyan.toArgb(),
        fillOpacity = 0.3
    )
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
fun InfoBox(airport: Airport, state: HomeViewModel.UiState, onClose: () -> Unit) = Box(
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .background(Color.White.copy(alpha = 0.6f))
        .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
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
            Text("${airport.name} / ${airport.icao.code}", fontWeight = FontWeight.Bold)
            IconButton(onClick = { onClose() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close icon")
            }
        }

        Column(Modifier.padding(start = 10.dp)) {

            Text(
                "Lat: ${airport.position.latitude}",
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(
                "Lon: ${airport.position.longitude}",
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(15.dp))

            when (state.sun) {
                is LoadingState.Loading -> Text(text = "Loading sun")
                is LoadingState.Error -> Text(text = state.sun.message)
                is LoadingState.Success -> state.sun.value?.let { SunComposable(sun = it) }
            }
        }
    }
}


@Composable
fun SunComposable(sun: Sun) {
    Text(
        text = "Sun info:", style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold)
    )
    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(text = sun.sunrise, modifier = Modifier.height(IntrinsicSize.Min))

        Image(
            painter = painterResource(id = R.drawable.clearsky_polartwilight),
            contentDescription = "sunrise",
            Modifier
                .rotate(180F)
                .size(35.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))

        Text(text = sun.sunset)

        Image(
            painter = painterResource(id = R.drawable.clearsky_polartwilight),
            contentDescription = "sunset",
            Modifier.size(35.dp)
        )
        Text(text = "(LT)")
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
