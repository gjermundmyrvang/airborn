package no.uio.ifi.in2000.team18.airborn.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import no.uio.ifi.in2000.team18.airborn.R
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport

@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxHomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) =
    Column(modifier = Modifier.fillMaxSize()) {
        val state by homeViewModel.state.collectAsState()
        val airports = state.airports
        val startPos = airports.first().position
        var selectedAirport by remember { mutableStateOf(airports.first()) }
        var showInfoBox by remember { mutableStateOf(false) }
        Box {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = MapViewportState().apply {
                    setCameraOptions {
                        zoom(7.0)
                        center(Point.fromLngLat(startPos.longitude, startPos.latitude))
                        pitch(0.0)
                        bearing(0.0)
                    }
                },
            ) {
                airports.forEach { airport ->
                    Annotation(airport) {
                        showInfoBox = true
                        selectedAirport = it
                    }
                }
            }
            if (showInfoBox) {
                InfoBox(airport = selectedAirport) {
                    showInfoBox = false
                }
            }
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
            painter = painterResource(id = R.drawable.red_marker),
            contentDescription = "Marker",
            modifier = Modifier
                .size(20.dp)
                .clickable { onAirportClicked(airport) },
        )
    }
}

@Composable
fun InfoBox(airport: Airport, onClose: () -> Unit) = Box(
    modifier = Modifier
        .padding(16.dp)
        .height(300.dp)
        .fillMaxWidth()
        .background(Color.White.copy(alpha = 0.3f))
        .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
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
            Text("${airport.name} / ${airport.icao.code}", fontWeight = FontWeight.Bold)
            IconButton(onClick = { onClose() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close icon")
            }
        }
        Text("Latitude: ${airport.position.latitude}")
        Text("Longtitude: ${airport.position.longitude}")
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
