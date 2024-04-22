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
import androidx.compose.material3.LinearProgressIndicator
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
import no.uio.ifi.in2000.team18.airborn.data.repository.parsers.parseSigmet
import no.uio.ifi.in2000.team18.airborn.model.Sigmet
import no.uio.ifi.in2000.team18.airborn.model.SigmetType
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Airport
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Icao
import no.uio.ifi.in2000.team18.airborn.model.flightbrief.Position
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
            Column(Modifier.padding(top = 16.dp)) {
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
        PolygonAnnotation(points = listOf(sigmet.coordinates.map { it.toPoints() }),
            fillOutlineColorInt = Color.Black.toArgb(),
            fillColorInt = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.4f)
                .toArgb() else Color.Yellow.copy(
                alpha = 0.4f
            ).toArgb(),
            fillOpacity = 1.0,
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
        .fillMaxWidth()
        .background(
            color = if (sigmet.type == SigmetType.Airmet) Color.Cyan.copy(alpha = 0.6f) else Color.Yellow.copy(
                alpha = 0.6f
            )
        )
        .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
        .clip(RoundedCornerShape(5.dp))
) {
    IconButton(onClick = { onClose() }, modifier = Modifier.align(Alignment.TopEnd)) {
        Icon(
            imageVector = Icons.Filled.Close, contentDescription = "Close icon", tint = Color.Black
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Row {
            Text("Type: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.type.toString(), color = Color.Black)
        }
        Row {
            Text("Weathermessage: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.message.joinToString(" ") { it }, color = Color.Black)
        }
        Row {
            Text("Location: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.location, color = Color.Black)
        }
        Row {
            Text("Origin location: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.originatingLocation, color = Color.Black)
        }
        Row {
            Text("Region code: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.regionCode, color = Color.Black)
        }
        Row {
            Text("Issuing authority: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text(sigmet.issuingAuthority, color = Color.Black)
        }
        val issued = sigmet.dateTime
        val validFrom = sigmet.timeRange.first
        val validTo = sigmet.timeRange.second
        Row {
            Text("Issued: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$issued", color = Color.Black)
        }
        Row {
            Text("Valid from: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$validFrom", color = Color.Black)
        }
        Row {
            Text("Valid to: ", color = Color.Black, fontWeight = FontWeight.Bold)
            Text("$validTo", color = Color.Black)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TestSigmetInfoBox() {
    SigmetInfoBox(
        sigmet = parseSigmet(
            """
        ZCZC
        WSNO36 ENMI 211336
        ENOB SIGMET M01 VALID 211400/211800 ENMI-
        ENOB BODOE OCEANIC FIR SEV MTW FCST WI N7950 E01030 - N8000 E01730 - N7900 E02000 - N7900 E01100 - N7950 E01030 SFC/FL250 STNR INTSF=
    """.trimIndent()
        ).expect()
    ) {

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
    IconButton(
        onClick = { onClose() }, Modifier.align(Alignment.TopEnd)
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Close icon",
            tint = MaterialTheme.colorScheme.background
        )
    }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        Text(
            "${airport.name} / ${airport.icao.code}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.background
        )
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
            is LoadingState.Loading -> LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            is LoadingState.Error -> Text(
                text = "Failed to retrive sundata, could be because of midnightsun",
                color = MaterialTheme.colorScheme.background
            )

            is LoadingState.Success -> state.sun.value?.let { SunComposable(sun = it) }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun TestInfoBox() {
    InfoBox(
        airport = Airport(
            icao = Icao("ENGM"), name = "Gardermoen", position = Position(59.11, 11.59)
        ), state = HomeViewModel.UiState()
    ) { }
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
